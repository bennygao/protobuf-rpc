package cc.devfun.pbrpc.mina;

import static java.lang.String.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cc.devfun.pbrpc.*;
import com.google.protobuf.nano.MessageNano;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessage;

public class ProtobufRpcHandler implements IoHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Endpoint endpoint;
	private Map<Long, Map<Integer, ResponseHandle>> sessionStampsMap;
	private Lock lock;
	private SessionStateMonitor stateMonitor;
	private Message cancelMessage;

	public ProtobufRpcHandler(Endpoint endpoint, SessionStateMonitor monitor) {
		this.endpoint = endpoint;
		this.stateMonitor = monitor;
		this.sessionStampsMap = new HashMap<>();
		this.lock = new ReentrantLock();

		cancelMessage = new ResponseMessage(0, 0, null);
		cancelMessage.setRpcCanceled();
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

	@Override
	public void exceptionCaught(IoSession session, Throwable e)
			throws Exception {
		logger.warn("transport session caught exception.", e);
		session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object arg) throws Exception {
		Message message = (Message) arg;
		logger.debug(format(">> IoSession(%d) received message: " + message, session.getId()));
		int serviceId = message.getServiceId();
		int stamp = message.getStamp();
		MessageNano argument = message.getArgument();
		if (message.isRequest()) {
			Message response = null;
			ServiceRegistry registry = endpoint.getRegistry(serviceId);
			if (registry != null && registry.hasImplementation()) {
				try {
					MessageNano returnsValue = registry.invokeService(serviceId, argument, new MinaServerSession(session));
					response = new ResponseMessage(serviceId, stamp, returnsValue);
				} catch (Throwable t) {
					response = new ResponseMessage(message.getServiceId(), message.getStamp(), null);
					response.setServiceException();
				}
			} else { // 请求的服务未注册
				response = new ResponseMessage(message.getServiceId(), message.getStamp(), null);
				response.setServiceNotExist();

			}
			session.write(response);
		} else {
				if (message.isServiceNotExist()) {
					logger.error("remote endpoint doesn't supply service for serviceId " + message.getServiceId());
				}

				ResponseHandle handle = getStampHandle(session, message.getStamp());
				if (handle == null) {
					logger.warn("response's handle unregistered: " + message);
				} else {
					handle.onResponse(message);
				}

		}
	}

	@Override
	public void messageSent(IoSession arg0, Object obj) throws Exception {
		Message message = (Message) obj;
		logger.debug(format("<< IoSession(%d) sent messag: " + message,
				arg0.getId()));
		addStampHandle(arg0, message);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.debug("-- IoSession(%d) closed.", session.getId());
		// 删除session上绑定的receiver对象
		session.removeAttribute(IoBufferMessageReceiver.KEY);

		// 清理session上注册的响应事件处理
		clearStampHandle(session);

		stateMonitor.sessionClosed(session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// 把Receiver对象绑定在session上
		session.setAttribute(IoBufferMessageReceiver.KEY,
				new IoBufferMessageReceiver(session, endpoint));

		stateMonitor.sessionCreated(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		stateMonitor.sessionIdle(session, status);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		stateMonitor.sessionOpened(session);
	}

	@Override
	public void inputClosed(IoSession ioSession) throws Exception {
		logger.debug("## IoSession(%d) inputClosed.", ioSession.getId());

		// IMPORTANT! 客户端TCP链接如果是正常调用shutdown()再close()的话，会产生这个事件。
		// 如果不调用ioSession.close()则会一直产生这个事件。
		ioSession.close(true);

	}
}
