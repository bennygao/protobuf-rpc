package com.yingshibao.foundation.rpc.mina;

import static java.lang.String.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessage;
import com.yingshibao.foundation.rpc.Endpoint;
import com.yingshibao.foundation.rpc.Message;
import com.yingshibao.foundation.rpc.ResponseHandle;
import com.yingshibao.foundation.rpc.ServiceRegistry;

public class ProtobufRpcHandler implements IoHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Endpoint endpoint;
	private Map<Long, Map<Integer, ResponseHandle>> sessionStampsMap;
	private Lock lock;
	private Message cancelMessage = new Message(Message.Type.cancel);

	public ProtobufRpcHandler(Endpoint endpoint) {
		this.endpoint = endpoint;
		this.sessionStampsMap = new HashMap<Long, Map<Integer, ResponseHandle>>();
		this.lock = new ReentrantLock();
	}

	private void addStampHandle(IoSession ioSession, Message message) {
		lock.lock();
		try {
			ResponseHandle handle = message.getResponseHandle();
			if (handle != null) {
				Map<Integer, ResponseHandle> stampsMap = sessionStampsMap
						.get(ioSession.getId());
				if (stampsMap == null) {
					stampsMap = new HashMap<Integer, ResponseHandle>();
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
			handle.execute(cancelMessage);
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable e)
			throws Exception {
		logger.warn("transport session caught exception.", e);
		session.close(false);
	}

	@Override
	public void messageReceived(IoSession session, Object arg) throws Exception {
		Message message = (Message) arg;
		logger.debug(format(">> ioSession(%d) received message: " + message,
				session.getId()));
		int serviceId = message.getServiceId();

		if (message.getStage() == Message.STAGE_REQUEST) {
			ServiceRegistry registry = endpoint.getRegistry(serviceId);
			GeneratedMessage returnsValue = registry.invokeService(serviceId,
					message.getArgument(), new MinaIoSession(session));
			Message response = new Message(message.getServiceId(),
					message.getStamp(), Message.STAGE_RESPONSE, returnsValue);
			session.write(response);
		} else {
			ResponseHandle handle = getStampHandle(session, message.getStamp());
			if (handle == null) {
				logger.warn("response's handle unregistered: " + message);
			} else {
				handle.execute(message);
			}
		}
	}

	@Override
	public void messageSent(IoSession arg0, Object obj) throws Exception {
		Message message = (Message) obj;
		logger.debug(format("<< ioSession(%d) sent messag: " + message,
				arg0.getId()));
		addStampHandle(arg0, message);
	}

	@Override
	public void sessionClosed(IoSession ioSession) throws Exception {
		logger.debug("-- ioSession(%d) closed.", ioSession.getId());
		// 删除session上绑定的receiver对象
		ioSession.removeAttribute(IoBufferMessageReceiver.KEY);

	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// 清理session上注册的响应事件处理
		clearStampHandle(session);

		// 把Receiver对象绑定在session上
		session.setAttribute(IoBufferMessageReceiver.KEY,
				new IoBufferMessageReceiver(session, endpoint));

	}

	@Override
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
	}

	@Override
	public void sessionOpened(IoSession arg0) throws Exception {
	}

	@Override
	public void inputClosed(IoSession ioSession) throws Exception {
		logger.debug("## ioSession(%d) inputClosed.", ioSession.getId());

		// IMPORTANT! 客户端TCP链接如果是正常调用shutdown()再close()的话，会产生这个事件。
		// 如果不调用ioSession.close()则会一直产生这个事件。
		ioSession.close(true);

	}
}
