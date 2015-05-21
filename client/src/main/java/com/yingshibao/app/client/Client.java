package com.yingshibao.app.client;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.TextFormat;
import com.yingshibao.app.idl.Push;
import com.yingshibao.app.idl.RegisterResult;
import com.yingshibao.app.idl.UserInfo;
import com.yingshibao.app.idl.UserManager;
import com.yingshibao.foundation.rpc.Endpoint;
import com.yingshibao.foundation.rpc.nio.NioSocketEndpoint;
import com.yingshibao.foundation.rpc.nio.NioSocketSession;


public class Client {
	public static void main(String[] args) throws Exception {
		Client client = new Client("localhost", 10000);
		for (int i = 0; i < 10; ++i) {
			client.runTest();
		}
		client.stop();
	}

	private NioSocketEndpoint endpoint;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private AtomicInteger counter = new AtomicInteger(0);

	public Client(String addr, int port) throws Exception {
		endpoint = new NioSocketEndpoint();
		endpoint.registerService(new Push(new PushImpl()));
		endpoint.registerService(new UserManager());
		endpoint.connect(addr, port);
		endpoint.start();
	}

	public void runTest() throws Exception {
		UserManager.Client client = new UserManager.Client(new NioSocketSession(endpoint));
		UserInfo userInfo = UserInfo.newBuilder().setChannelName("360应用商店")
				.setPhone("13810773316").setExamType(1).setNickName("Johnn")
				.build();
		
		counter.incrementAndGet();
		client.registerNewUser(userInfo, new Endpoint.Callback() {
			@Override
			public void onResponse(GeneratedMessage response, Endpoint.RpcState state) {
				if (state == Endpoint.RpcState.success) {
					RegisterResult result = (RegisterResult) response;
					logger.info("ASYNC:注册用户返回:" + TextFormat.printToUnicodeString(result));
					counter.decrementAndGet();
				} else if (state == Endpoint.RpcState.service_not_exist) {
					logger.info("ASYNC:对方endpoint不提供registerNewUser服务");
				} else if (state == Endpoint.RpcState.rpc_canceled) {
					logger.info("ASYNC:RPC调用被取消");
				}
			}
		});
		
		RegisterResult result = client.registerNewUser(userInfo);
		logger.info("SYNC:注册用户返回:" + TextFormat.printToUnicodeString(result));

	}
	
	public void stop() throws Exception {
		while (counter.get() > 0) {
			Thread.sleep(1000L);
		}
		
		endpoint.stop();
	}
}
