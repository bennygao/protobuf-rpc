package cc.devfun.pbrpc.mina;

import static java.lang.String.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cc.devfun.pbrpc.*;
import com.google.protobuf.nano.MessageNano;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtobufRpcHandler implements IoHandler {
    private final static String KEY_CHECKING_HEARTBEAT = "CHECKING_HEARTBEAT";

    private Logger logger = LoggerFactory.getLogger(getClass());
    private Endpoint endpoint;
    private Map<Long, Map<Integer, ResponseHandle>> sessionStampsMap;
    private Lock lock;
    private SessionStateMonitor stateMonitor;
    private Message cancelMessage;
    private Message heartbeatMessage;
    private List<ExecutorService> executors;

    public ProtobufRpcHandler(Endpoint endpoint, List<ExecutorService> executors, SessionStateMonitor monitor) {
        this.endpoint = endpoint;
        this.executors = executors;
        this.stateMonitor = monitor;
        this.sessionStampsMap = new HashMap<>();
        this.lock = new ReentrantLock();

        cancelMessage = new ResponseMessage(0, 0, null);
        cancelMessage.setRpcCanceled();

        heartbeatMessage = Message.createHeartbeatMessage();
    }

    private void addStampHandle(IoSession ioSession, Message message) {
        lock.lock();
        try {
            ResponseHandle handle = message.getResponseHandle();
            if (handle != null) {
                Map<Integer, ResponseHandle> stampsMap = sessionStampsMap
                        .get(ioSession.getId());
                if (stampsMap == null) {
                    stampsMap = new HashMap<>();
                    sessionStampsMap.put(ioSession.getId(), stampsMap);
                }
                stampsMap.put(message.getStamp(), handle);
            }
        } finally {
            lock.unlock();
        }
    }

    private ResponseHandle getStampHandle(IoSession ioSession, int stamp) {
        lock.lock();
        try {
            Map<Integer, ResponseHandle> stampsMap = sessionStampsMap
                    .get(ioSession.getId());
            return stampsMap == null ? null : stampsMap.remove(stamp);
        } finally {
            lock.unlock();
        }
    }

    private void clearStampHandle(IoSession ioSession) {
        Map<Integer, ResponseHandle> stampsMap = null;
        lock.lock();
        try {
            stampsMap = sessionStampsMap.remove(ioSession.getId());
        } finally {
            lock.unlock();
        }

        if (stampsMap == null) {
            return;
        }

        for (ResponseHandle handle : stampsMap.values()) {
            handle.onResponse(cancelMessage);
        }
    }

    private ExecutorService getExecutor(IoSession session) {
        return executors.get((int) session.getId() % executors.size());
    }

    @Override
    public void exceptionCaught(final IoSession session, Throwable e)
            throws Exception {
        logger.warn("transport session caught exception.", e);
        getExecutor(session).submit(new Runnable() {
            @Override
            public void run() {
                session.close(true);
            }
        });
    }

    private void sendResponse(IoSession session, Message response) {
        MessageNano arg = response.getArgument();
        if (arg != null) {
            try {
                arg.getSerializedSize();
            } catch (Throwable t) {
                logger.error("response's argument message has null value field. serviceId= " + response, t);
                onSendResponseError(session, response);
                return;
            }
        }

        session.write(response);

    }

    private void onSendResponseError(IoSession session, Message response) {
        Message newone = new ResponseMessage(response.getServiceId(), response.getStamp(), null);
        newone.setServiceException();
        session.write(newone);
    }

    private void onHeartbeatMessage(IoSession session, Message message) {
        if (message.isRequest()) {
            sendResponse(session, message.createResponse(null));
        } else {
            session.setAttribute(KEY_CHECKING_HEARTBEAT, false);
        }
    }

    @Override
    public void messageReceived(final IoSession session, Object arg) throws Exception {
        logger.debug(format(">> IoSession(%d) received message: " + arg, session.getId()));
        final Message message = (Message) arg;
        final int serviceId = message.getServiceId();
        if (serviceId == 0) { // heartbeat消息
            onHeartbeatMessage(session, message);
            return;
        }

        final int stamp = message.getStamp();
        final MessageNano argument = message.getArgument();
        getExecutor(session).submit(new Runnable() {
            @Override
            public void run() {
                if (message.isRequest()) {
                    Message response = null;
                    ServiceRegistry registry = endpoint.getRegistry(serviceId);
                    if (registry != null && registry.hasImplementation()) {
                        try {
                            MessageNano returnsValue = registry.invokeService(serviceId, argument,
                                    new MinaServerSession(session));
                            response = new ResponseMessage(serviceId, stamp, returnsValue);
                        } catch (Throwable t) {
                            response = new ResponseMessage(serviceId, stamp, null);
                            response.setServiceException();
                        }
                    } else { // 请求的服务未注册
                        response = new ResponseMessage(serviceId, stamp, null);
                        response.setServiceNotExist();
                    }
                    sendResponse(session, response);
                } else {
                    if (message.isServiceNotExist()) {
                        logger.error("remote endpoint doesn't supply service for serviceId " + serviceId);
                    }

                    ResponseHandle handle = getStampHandle(session, stamp);
                    if (handle == null) {
                        logger.warn("response's handle unregistered: " + message);
                    } else {
                        handle.onResponse(message);
                    }
                }
            }
        });
    }

    @Override
    public void messageSent(IoSession session, Object obj) throws Exception {
        Message message = (Message) obj;
        logger.debug(format("<< IoSession(%d) sent messag: " + message,
                session.getId()));
        if (message.getServiceId() != 0) { // 应用消息
            addStampHandle(session, message);
        }
    }

    @Override
    public void sessionClosed(final IoSession session) throws Exception {
        logger.debug("-- IoSession(%d) closed.", session.getId());
        // 删除session上绑定的receiver对象
        session.removeAttribute(IoBufferMessageReceiver.KEY);

        // 清理session上注册的响应事件处理
        clearStampHandle(session);

        if (stateMonitor != null) {
            getExecutor(session).submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        stateMonitor.sessionClosed(session);
                    } catch (Exception e) {
                        logger.error("sessionClosed monitor error.", e);
                    }
                }
            });
        }

    }

    @Override
    public void sessionCreated(final IoSession session) throws Exception {
        // 把Receiver对象绑定在session上
        session.setAttribute(IoBufferMessageReceiver.KEY,
                new IoBufferMessageReceiver(session, endpoint));

        if (stateMonitor != null) {
            getExecutor(session).submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        stateMonitor.sessionCreated(session);
                    } catch (Exception e) {
                        logger.error("sessionCreated monitor error.", e);
                    }
                }
            });
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        boolean isCheckingHeartbeat = (boolean) session.getAttribute("CHECKING_HEARTBEAT", false);
        if (!isCheckingHeartbeat) {
            session.write(heartbeatMessage);
            session.setAttribute("CHECKING_HEARTBEAT", true);
            logger.info("begin check heartbeat... " + session);
        } else { // heartbeat检测失败
            logger.error("session check heartbeat failed. " + session);
            session.close(true);
        }
    }

    @Override
    public void sessionOpened(final IoSession session) throws Exception {
        if (stateMonitor != null) {
            getExecutor(session).submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        stateMonitor.sessionOpened(session);
                    } catch (Exception e) {
                        logger.error("sessionOpened monitor error.", e);
                    }
                }
            });
        }
    }

    @Override
    public void inputClosed(IoSession ioSession) throws Exception {
        logger.debug("## IoSession(%d) inputClosed.", ioSession.getId());

        // IMPORTANT! 客户端TCP链接如果是正常调用shutdown()再close()的话，会产生这个事件。
        // 如果不调用ioSession.close()则会一直产生这个事件。
        ioSession.close(true);

    }
}
