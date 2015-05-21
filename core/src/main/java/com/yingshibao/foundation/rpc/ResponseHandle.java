package com.yingshibao.foundation.rpc;

import java.util.concurrent.LinkedBlockingQueue;

import com.yingshibao.foundation.rpc.Endpoint.Callback;
import com.yingshibao.foundation.rpc.Endpoint.RpcStrategy;

public class ResponseHandle implements Runnable {
	private RpcStrategy strategy;
	private LinkedBlockingQueue<Message> queue;
	private Callback callback;
	private Message response;

	public ResponseHandle(LinkedBlockingQueue<Message> queue) {
		this.strategy = RpcStrategy.sync;
		this.queue = queue;
		this.response = null;
	}

	public RpcStrategy getStrategy() {
		return this.strategy;
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
		if (strategy == RpcStrategy.sync) { // 同步调用返回
			try {
				queue.put(response);
			} catch (Exception e) {

			}
		} else { // 异步调用返回
			if (callback != null ) { // callback可能为null，表示调用者忽略响应（一般用于服务器端主动push消息给客户端）
				if (response.isRpcCanceled()) {
					callback.onResponse(null, Endpoint.RpcState.rpc_canceled);
				} else if (response.isServiceNotExist()) {
					callback.onResponse(null, Endpoint.RpcState.service_not_exist);
				} else {
					callback.onResponse(response.getArgument(), Endpoint.RpcState.success);
				}
			}
		}
	}

	public void onResponse(Message response) {
		this.response = response;
		run();
	}
}