package com.yingshibao.foundation.rpc.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yingshibao.foundation.rpc.Message;
import com.yingshibao.foundation.rpc.RpcSession;

public class NioSocketSession implements RpcSession {
	private NioSocketEndpoint endpoint;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public NioSocketSession(NioSocketEndpoint endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public void sendMessage(Message message) {
		try {
			endpoint.sendMessage(message);
		} catch (Exception e) {
			logger.error("send message error." + message, e);
		}
		
	}

}
