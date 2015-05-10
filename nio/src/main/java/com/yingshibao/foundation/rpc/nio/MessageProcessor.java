package com.yingshibao.foundation.rpc.nio;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessage;
import com.yingshibao.foundation.rpc.Message;
import com.yingshibao.foundation.rpc.ResponseHandle;
import com.yingshibao.foundation.rpc.ServiceRegistry;

public class MessageProcessor implements Runnable {
	private final static int DEFAULT_PROCESSORS_NUM = 2;

	private int processorsNum;
	private Thread[] processors;
	private LinkedBlockingQueue<Message> messageQueue;
	private NioSocketEndpoint endpoint;
	private Map<Integer, ResponseHandle> stampsMap;
	private Message stopMessage;
	private Message cancelMessage;
	private Logger logger;

	public MessageProcessor(NioSocketEndpoint endpoint, int processorsNum) {
		this.processorsNum = processorsNum;
		this.endpoint = endpoint;
		this.processors = new Thread[processorsNum];
		this.messageQueue = new LinkedBlockingQueue<Message>();
		this.stampsMap = new ConcurrentHashMap<Integer, ResponseHandle>();
		this.stopMessage = new Message(Message.Type.stop);
		this.cancelMessage = new Message(Message.Type.cancel);
		this.logger = LoggerFactory.getLogger(getClass());
	}

	public MessageProcessor(NioSocketEndpoint endpoint) {
		this(endpoint, DEFAULT_PROCESSORS_NUM);
	}

	public void start() {
		stop();

		for (int i = 0; i < processorsNum; ++i) {
			processors[i] = new Thread(this);
			processors[i].start();
		}
	}

	public void stop() {
		for (Thread proc : processors) {
			if (proc == null) {
				continue;
			} else if (proc.isAlive()) {
				messageQueue.add(stopMessage);
			}
		}

		for (int i = 0; i < processors.length; ++i) {
			if (processors[i] != null) {
				try {
					processors[i].join();
				} catch (Exception e) {

				}
				processors[i] = null;
			}
		}

		messageQueue.clear();


		for (ResponseHandle handle : stampsMap.values()) {
//			handle.execute(cancelMessage);
		}

		stampsMap.clear();
	}

	public void onMessage(Message message) {
		messageQueue.add(message);
	}

	public void registerMessage(Message message) {
		ResponseHandle handle = message.getResponseHandle();
		if (handle != null) {
			stampsMap.put(message.getStamp(), handle);
		}
	}
	
	private void procApplicationMessage(Message message) throws IOException {
		int serviceId = message.getServiceId();
		
		if (message.getStage() == Message.STAGE_RESPONSE) { // 处理响应
			ResponseHandle handle = stampsMap.remove(message
					.getStamp());
			if (handle == null) {
				logger.error("未注册处理的消息: " + message);
			} else {
//				handle.execute(message);
			}
		} else { // 处理请求
			ServiceRegistry registry = endpoint
					.getRegistry(serviceId);
			if (registry == null) {
				logger.error("未注册的serviceId: " + serviceId);
			} else {
				GeneratedMessage returns = registry.invokeService(serviceId,
						message.getArgument(),
						new NioSocketSession(endpoint));
				Message returnsMessage = new Message(serviceId,
						message.getStamp(), Message.STAGE_RESPONSE,
						returns);
				endpoint.sendResponse(returnsMessage);
			}
		}
	}

	@Override
	public void run() {
		Message message = null;
		Message.Type type = null;

		while (true) {
			try {
				message = messageQueue.take();
				type = message.getType();
				if (type == Message.Type.stop) {
					return;
				} else if (type == Message.Type.application) {
					procApplicationMessage(message);
				} else if (type == Message.Type.cancel) {
					throw new UnsupportedOperationException();
				}
			} catch (Exception e) {
				logger.error("处理消息错误：" + message);
			}
		}
	}
}