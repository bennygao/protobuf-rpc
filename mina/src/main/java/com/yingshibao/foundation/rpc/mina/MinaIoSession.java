package com.yingshibao.foundation.rpc.mina;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.session.IoSession;

import com.yingshibao.foundation.rpc.Message;
import com.yingshibao.foundation.rpc.RpcSession;

public class MinaIoSession implements RpcSession {
	private static AtomicInteger STAMP = new AtomicInteger(0);
	
	private IoSession ioSession;
	
	public MinaIoSession(IoSession ioSession) {
		this.ioSession = ioSession;
	}

	public void close() {
		ioSession.close(true);
	}
	
	@Override
	public void sendRequest(Message message) {
		message.setStamp(STAMP.incrementAndGet());
		message.setStage(Message.STAGE_REQUEST);
		ioSession.write(message);
	}

	public Object getAttribute(Object o) {
		return ioSession.getAttribute(o);
	}

	public Object setAttribute(Object o, Object o1) {
		return ioSession.setAttribute(o, o1);
	}

	public Object removeAttribute(Object o) {
		return ioSession.removeAttribute(o);
	}

	public boolean containsAttribute(Object o) {
		return ioSession.containsAttribute(o);
	}

	public boolean isConnected() {
		return ioSession.isConnected();
	}

	public SocketAddress getRemoteAddress() {
		return ioSession.getRemoteAddress();
	}

	public SocketAddress getLocalAddress() {
		return ioSession.getLocalAddress();
	}
}
