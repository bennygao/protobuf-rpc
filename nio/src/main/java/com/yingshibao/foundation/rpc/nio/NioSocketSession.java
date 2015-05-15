package com.yingshibao.foundation.rpc.nio;

import com.yingshibao.foundation.rpc.Message;
import com.yingshibao.foundation.rpc.RpcSession;

public class NioSocketSession implements RpcSession {
	private NioSocketEndpoint endpoint;
	
	public NioSocketSession(NioSocketEndpoint endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public void sendMessage(Message message) {
		try {
			endpoint.sendMessage(message);
		} catch (Exception e) {
			System.err.println("send message error." + message);
			e.printStackTrace();
		}
		
	}

}
