package com.yingshibao.app.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.yingshibao.app.idl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.TextFormat;
import cc.devfun.pbrpc.Endpoint;
import cc.devfun.pbrpc.nio.NioClientEndpoint;
import cc.devfun.pbrpc.nio.NioClientSession;


public class Client {
	public static void main(String[] args) throws Exception {
		Client client = new Client("localhost", 10000);
		client.doTest();
		client.stop();
	}


    final static int LOOP_COUNT = 100;
	private NioClientEndpoint endpoint;
	private Logger logger = LoggerFactory.getLogger(getClass());
    String remoteAddr;
    int remotePort;
    CountDownLatch latch;

	public Client(String addr, int port) throws Exception {
        this.remoteAddr = addr;
        this.remotePort = port;

		endpoint = new NioClientEndpoint();
		endpoint.registerService(new Push(new PushImpl()));
		endpoint.registerService(new UserManager());

	}

    public void doTest() throws Exception {
        endpoint.connect(remoteAddr, remotePort);
        endpoint.start();

        latch = new CountDownLatch(2 * LOOP_COUNT);
        for (int i = 0; i < LOOP_COUNT; ++i) {
            testUnregisteredService();
            testRegisteredService();
        }
    }

	public void testRegisteredService() throws Exception {
		UserManager.Client client = new UserManager.Client(new NioClientSession(endpoint));
		UserInfo userInfo = UserInfo.newBuilder().setChannelName("360应用商店")
				.setPhone("13810773316").setExamType(1).setNickName("Johnn")
				.build();

		client.registerNewUser(userInfo, new Endpoint.Callback() {
			@Override
			public void onResponse(GeneratedMessage response) {
				RegisterResult result = (RegisterResult) response;
				logger.info("ASYNC:注册用户返回:" + TextFormat.printToUnicodeString(result));
                latch.countDown();
			}

			@Override
			public void onError(Endpoint.RpcError error) {
                latch.countDown();
                if (error == Endpoint.RpcError.rpc_canceled) {
                    logger.error("ASYNC:RPC调用被取消");
                } else if (error == Endpoint.RpcError.service_not_exist) {
                    logger.error("ASYNC:对方endpoint不提供getCourseList服务");
                } else if (error == Endpoint.RpcError.service_exception) {
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
			CourseManager.Client client = new CourseManager.Client(new NioClientSession(endpoint));
			CourseList courseList = client.getCourseList(courseType);
			logger.info("SYNC:注册用户返回:" + TextFormat.printToUnicodeString(courseList));
		} catch (Exception e) {
			logger.error("SYNC: 获取课程列表出错。", e);
		}

		CourseType courseType = CourseType.newBuilder().setNum(10).setPageNum(1).setCourseType(1).build();
		CourseManager.Client client = new CourseManager.Client(new NioClientSession(endpoint));

		client.getCourseList(courseType, new Endpoint.Callback() {
			@Override
			public void onResponse(GeneratedMessage response) {
                latch.countDown();
				CourseList courseList = (CourseList) response;
				logger.error("SYNC:注册用户返回:" + TextFormat.printToUnicodeString(courseList));
			}

			@Override
			public void onError(Endpoint.RpcError error) {
                latch.countDown();
				if (error == Endpoint.RpcError.rpc_canceled) {
					logger.error("ASYNC:RPC调用被取消");
				} else if (error == Endpoint.RpcError.service_not_exist) {
					logger.error("ASYNC:对方endpoint不提供getCourseList服务");
				} else if (error == Endpoint.RpcError.service_exception) {
					logger.error("ASNYC:对方服务处理异常");
				} else {
					logger.error("ASNYC:未知错误");
				}
			}
		});

	}
	
	public void stop() throws Exception {
		latch.await();
		endpoint.stop();
	}
}
