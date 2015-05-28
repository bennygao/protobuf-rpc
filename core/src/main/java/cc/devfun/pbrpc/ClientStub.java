package cc.devfun.pbrpc;

import com.google.protobuf.nano.MessageNano;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


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

    private void checkArgument(MessageNano arg) throws Exception {
        try {
            arg.getSerializedSize();
        } catch (Throwable t) {
            throw new IllegalArgumentException("RPC input argument has null value field. message=" + arg);
        }
    }

	protected MessageNano syncRpc(int serviceId, MessageNano arg)
			throws Exception {
        checkArgument(arg);
		Message request = new RequestMessage(serviceId, STAMP.incrementAndGet(), arg);
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
		
		if (response.isRpcCanceled()) {
			throw new InterruptedException("synchronized rpc be canceled.");
		} else if (response.isServiceNotExist()) {
			throw new NoSuchMethodException("remote endpoint doesn't register invoked service.");
		} else if (response.isServiceException()) {
			throw new RuntimeException("remote endpoint service process occured exception.");
		} else if (response.isIllegalArgument()) {
			throw new IllegalArgumentException("request's argument message has error.");
		} else {
			return response.getArgument();
		}
	}

	protected void asyncRpc(int serviceId, MessageNano arg,
			Endpoint.Callback callback) throws Exception {
        checkArgument(arg);
		Message request = new RequestMessage(serviceId, STAMP.incrementAndGet(), arg);
		request.setResponseHandle(new ResponseHandle(callback));
		session.sendMessage(request);
	}
}
