package com.yingshibao.app.server;

import java.net.InetSocketAddress;

import com.yingshibao.app.idl.CourseManager;
import cc.devfun.pbrpc.mina.SessionStateMonitor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yingshibao.app.idl.Push;
import com.yingshibao.app.idl.UserManager;
import cc.devfun.pbrpc.mina.MinaServerEndpoint;

public class Server {
	static class ServerSessionMonitor implements SessionStateMonitor {
		Logger logger = LoggerFactory.getLogger(getClass());
		@Override
		public void sessionClosed(IoSession session) throws Exception {
			logger.info("++++ Session closed:" + session);
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			logger.info("++++ Session created:" + session);
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			logger.info("++++ Session opened:" + session);
		}
	}

	public static void main(String[] args) throws Exception {
		Logger logger = LoggerFactory.getLogger(Server.class);
		MinaServerEndpoint endpoint = new MinaServerEndpoint("MinaTcp", new InetSocketAddress(10000), 8, 5, new ServerSessionMonitor());

		endpoint.registerService(new UserManager(new UserManagerImpl()));
		endpoint.registerService(new Push());
		endpoint.registerService(new CourseManager());
		
		endpoint.start();
		logger.info("Server started.");
	}

}
