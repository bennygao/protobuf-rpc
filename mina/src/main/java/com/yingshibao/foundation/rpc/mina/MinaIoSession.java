package com.yingshibao.foundation.rpc.mina;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.session.IoSession;

import com.yingshibao.foundation.rpc.Message;
import com.yingshibao.foundation.rpc.RpcSession;

public class MinaIoSession implements RpcSession {
	private static AtomicInteger STAMP = new AtomicInteger(0);
	
	private IoSession ioSession;
	
	public MinaIoSession(IoSession ioSession) {
		this.ioSession = ioSession;
	}
	
	@Override
	public void sendRequest(Message message) {
		message.setStamp(STAMP.incrementAndGet());
		message.setStage(Message.STAGE_REQUEST);
		ioSession.write(message);
	}
}
