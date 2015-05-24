package com.yingshibao.app.client;

import java.util.concurrent.atomic.AtomicInteger;

import com.yingshibao.app.idl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.TextFormat;
import cc.devfun.pbrpc.Endpoint;
import cc.devfun.pbrpc.nio.NioSocketEndpoint;
import cc.devfun.pbrpc.nio.NioSocketSession;


public class Client {
	public static void main(String[] args) throws Exception {
		Client client = new Client("localhost", 10000);
		for (int i = 0; i < 100; ++i) {
			client.testUnregisteredService();
			client.testRegisteredService();

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

	public void testRegisteredService() throws Exception {
		UserManager.Client client = new UserManager.Client(new NioSocketSession(endpoint));
		UserInfo userInfo = UserInfo.newBuilder().setChannelName("360应用商店")
				.setPhone("13810773316").setExamType(1).setNickName("Johnn")
				.build();
		
		counter.incrementAndGet();
		client.registerNewUser(userInfo, new Endpoint.Callback() {
			@Override
			public void onResponse(GeneratedMessage response) {
				RegisterResult result = (RegisterResult) response;
				logger.info("ASYNC:注册用户返回:" + TextFormat.printToUnicodeString(result));
				counter.decrementAndGet();
			}

			@Override
			public void onError(Endpoint.RpcState state) {
                counter.decrementAndGet();
                if (state == Endpoint.RpcState.rpc_canceled) {
                    logger.error("ASYNC:RPC调用被取消");
                } else if (state == Endpoint.RpcState.service_not_exist) {
                    logger.error("ASYNC:对方endpoint不提供getCourseList服务");
                } else if (state == Endpoint.RpcState.service_exception) {
                    logger.error("ASNYC:对方服务处理异常");
                } else {
                    logger.error("ASNYC:未知错误");
                }
			}
		});
		
		RegisterResult result = client.registerNewUser(userInfo);
		logger.info("SYNC:注册用户返回:" + TextFormat.printToUnicodeString(result));

	}

	public void testUnregisteredService() throws Exception {
		try {
			CourseType courseType = CourseType.newBuilder().setNum(10).setPageNum(1).setCourseType(1).build();
			CourseManager.Client client = new CourseManager.Client(new NioSocketSession(endpoint));
			CourseList courseList = client.getCourseList(courseType);
			logger.info("SYNC:注册用户返回:" + TextFormat.printToUnicodeString(courseList));
		} catch (Exception e) {
			logger.error("SYNC: 获取课程列表出错。", e);
		}

		CourseType courseType = CourseType.newBuilder().setNum(10).setPageNum(1).setCourseType(1).build();
		CourseManager.Client client = new CourseManager.Client(new NioSocketSession(endpoint));

		counter.incrementAndGet();
		client.getCourseList(courseType, new Endpoint.Callback() {
			@Override
			public void onResponse(GeneratedMessage response) {
				CourseList courseList = (CourseList) response;
				logger.error("SYNC:注册用户返回:" + TextFormat.printToUnicodeString(courseList));
			}

			@Override
			public void onError(Endpoint.RpcState state) {
				counter.decrementAndGet();
				if (state == Endpoint.RpcState.rpc_canceled) {
					logger.error("ASYNC:RPC调用被取消");
				} else if (state == Endpoint.RpcState.service_not_exist) {
					logger.error("ASYNC:对方endpoint不提供getCourseList服务");
				} else if (state == Endpoint.RpcState.service_exception) {
					logger.error("ASNYC:对方服务处理异常");
				} else {
					logger.error("ASNYC:未知错误");
				}
			}
		});

	}
	
	public void stop() throws Exception {
		while (counter.get() > 0) {
			Thread.sleep(1000L);
		}
		
		endpoint.stop();
	}
}
