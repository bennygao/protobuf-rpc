package com.yingshibao.foundation.rpc;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.protobuf.GeneratedMessage;

public class ClientStub {
	private RpcSession session;
	private ConcurrentLinkedQueue<LinkedBlockingQueue<Message>> queuePool;

	public ClientStub(RpcSession session) {
		this.session = session;
		this.queuePool = new ConcurrentLinkedQueue<LinkedBlockingQueue<Message>>();
	}

	protected LinkedBlockingQueue<Message> borrowQueue() {
		LinkedBlockingQueue<Message> queue = queuePool.poll();
		if (queue == null) {
			queue = new LinkedBlockingQueue<Message>();
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
		Message request = new Message(serviceId, 0, Message.STAGE_REQUEST, arg);
		LinkedBlockingQueue<Message> queue = borrowQueue();
		queue.clear();
		request.setResponseHandle(new ResponseHandle(queue));
		Message response = null;
		try {
			session.sendRequest(request);
			response = queue.take();
		} catch (Exception e) {
			throw e;
		} finally {
			returnQueue(queue);
		}
		
		if (response.getType() != Message.Type.application) {
			throw new InterruptedException("synchronized rpc be canceled.");
		} else {
			return response.getArgument();
		}
	}

	protected void asyncRpc(int serviceId, GeneratedMessage arg,
			Endpoint.Callback callback) throws Exception {
		Message request = new Message(serviceId, 0, Message.STAGE_REQUEST, arg);
		request.setResponseHandle(new ResponseHandle(callback));
		session.sendRequest(request);
	}
}
