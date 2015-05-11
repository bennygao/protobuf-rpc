package com.yingshibao.foundation.rpc;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.LoggerFactory;

import com.yingshibao.foundation.rpc.Endpoint.Callback;
import com.yingshibao.foundation.rpc.Endpoint.RpcStrategy;

public class ResponseHandle implements Runnable {
	public RpcStrategy getStrategy() {
		return strategy;
	}

	public LinkedBlockingQueue<Message> getQueue() {
		return queue;
	}

	public Callback getCallback() {
		return callback;
	}

	private RpcStrategy strategy;
	private LinkedBlockingQueue<Message> queue;
	private Callback callback;
	private Message response;

	public ResponseHandle(LinkedBlockingQueue<Message> queue) {
		this.strategy = RpcStrategy.sync;
		this.queue = queue;
		this.response = null;
	}

	public ResponseHandle(Callback callback) {
		this.strategy = RpcStrategy.async;
		this.callback = callback;
		this.response = null;
	}

	public void assignResponse(Message message) {
		this.response = message;
	}

	@Override
	public void run() {
		if (strategy == RpcStrategy.sync) {
			try {
				queue.put(response);
			} catch (Exception e) {

			}
			LoggerFactory.getLogger(getClass()).info(
					"add message to queue -> " + queue.hashCode() + " size="
							+ queue.size());
		} else if (response.getType() == Message.Type.application
				&& callback != null) {
			// callback可能为null，表示调用者忽略响应（一般用于服务器端主动push消息给客户端）
			callback.onResponse(response.getArgument());
		}
	}

	public void onResponse(Message response) {
		this.response = response;
		run();
	}
}