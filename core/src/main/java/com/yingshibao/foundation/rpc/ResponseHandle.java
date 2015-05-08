package com.yingshibao.foundation.rpc;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.LoggerFactory;

import com.yingshibao.foundation.rpc.Endpoint.Callback;
import com.yingshibao.foundation.rpc.Endpoint.RpcStrategy;

public class ResponseHandle {
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

	public ResponseHandle(LinkedBlockingQueue<Message> queue) {
		this.strategy = RpcStrategy.sync;
		this.queue = queue;
	}

	public ResponseHandle(Callback callback) {
		this.strategy = RpcStrategy.async;
		this.callback = callback;
	}

	public void execute(Message message) {
		if (strategy == RpcStrategy.sync) {
			try {
				queue.put(message);
			} catch (Exception e) {

			}
			LoggerFactory.getLogger(getClass()).info(
					"add message to queue -> " + queue.hashCode() + " size="
							+ queue.size());
		} else if (message.getType() == Message.Type.application
				&& callback != null) {
			// callback可能为null，表示调用者忽略响应（一般用于服务器端主动push消息给客户端）
			callback.onResponse(message.getArgument());
		}
	}
}