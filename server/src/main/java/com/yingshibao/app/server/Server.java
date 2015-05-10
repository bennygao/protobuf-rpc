package com.yingshibao.app.server;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yingshibao.app.idl.Push;
import com.yingshibao.app.idl.UserManager;
import com.yingshibao.foundation.rpc.mina.MinaTcpEndpoint;

public class Server {

	public static void main(String[] args) throws Exception {
		Logger logger = LoggerFactory.getLogger(Server.class);
		MinaTcpEndpoint endpoint = new MinaTcpEndpoint("MinaTcp", new InetSocketAddress(10000));

		endpoint.registerService(new UserManager(new UserManagerImpl()));
		endpoint.registerService(new Push());
		
		endpoint.start();
		logger.info("Server started.");
	}

}
