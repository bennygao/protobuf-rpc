package cc.devfun.pbrpc;

import java.util.concurrent.LinkedBlockingQueue;

public class ResponseHandle implements Runnable {
	private LinkedBlockingQueue<Message> queue;
	private Endpoint.Callback callback;
	private Message response;

	public ResponseHandle(LinkedBlockingQueue<Message> queue) {
		this.queue = queue;
		this.callback = null;
		this.response = null;
	}

	public ResponseHandle(Endpoint.Callback callback) {
		this.queue = null;
		this.callback = callback;
		this.response = null;
	}

	public boolean isSynchronizedRpc() {
		return this.queue != null;
	}

	public void assignResponse(Message message) {
		this.response = message;
	}

	@Override
	public void run() {
		if (isSynchronizedRpc()) { // 同步调用返回
			try {
				queue.put(response);
			} catch (Exception e) {
                e.printStackTrace();
			}
		} else { // 异步调用返回
			if (callback != null ) { // callback可能为null，表示调用者忽略响应（一般用于服务器端主动push消息给客户端）
				if (response.isRpcCanceled()) {
                    callback.onError(Endpoint.RpcError.rpc_canceled);
				} else if (response.isServiceNotExist()) {
                    callback.onError(Endpoint.RpcError.service_not_exist);
				} else if (response.isServiceException()) {
                    callback.onError(Endpoint.RpcError.service_exception);
				} else {
					callback.onResponse(response.getArgument());
				}
			}
		}
	}

	public void onResponse(Message response) {
		this.response = response;
		run();
	}
}