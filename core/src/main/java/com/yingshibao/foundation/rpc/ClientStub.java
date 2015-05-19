package com.yingshibao.foundation.rpc;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.protobuf.GeneratedMessage;

public class ClientStub {
	private final static AtomicInteger STAMP = new AtomicInteger(0);

	private RpcSession session;
	private ConcurrentLinkedQueue<LinkedBlockingQueue<Message>> queuePool;

	public ClientStub(RpcSession session) {
		this.session = session;
		this.queuePool = new ConcurrentLinkedQueue<>();
	}

	protected LinkedBlockingQueue<Message> borrowQueue() {
		LinkedBlockingQueue<Message> queue = queuePool.poll();
		if (queue == null) {
			queue = new LinkedBlockingQueue<>();
		}

		return queue;
	}

	protected void returnQueue(LinkedBlockingQueue<Message> queue) {
		if (queue != null) {
			queue.clear();
			queuePool.add(queue);
		}
	}

	protected GeneratedMessage syncRpc(int serviceId, GeneratedMessage arg)
			throws Exception {
		Message request = new Message(serviceId, STAMP.incrementAndGet(), Message.STAGE_REQUEST, arg);
		LinkedBlockingQueue<Message> queue = borrowQueue();
		queue.clear();
		request.setResponseHandle(new ResponseHandle(queue));
		Message response = null;
		try {
			session.sendMessage(request);
			response = queue.take();
		} catch (Exception e) {
			throw e;
		} finally {
			returnQueue(queue);
		}
		
		if (response.getType() != Message.Type.application) {
			throw new InterruptedException("synchronized rpc be canceled.");
		} else if (response.getStage() == Message.STAGE_UNREGISTERED_SERVICE) {
			throw new NoSuchMethodException("remote endpoint doesn't register invoked service.");
		} else {
			return response.getArgument();
		}
	}

	protected void asyncRpc(int serviceId, GeneratedMessage arg,
			Endpoint.Callback callback) throws Exception {
		Message request = new Message(serviceId, STAMP.incrementAndGet(), Message.STAGE_REQUEST, arg);
		request.setResponseHandle(new ResponseHandle(callback));
		session.sendMessage(request);
	}
}
