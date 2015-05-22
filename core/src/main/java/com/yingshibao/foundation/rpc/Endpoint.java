package com.yingshibao.foundation.rpc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.protobuf.GeneratedMessage;

public class Endpoint {
	private Lock readLock;
	private Lock writeLock;
	private Map<Integer, ServiceRegistry> registryMap = new HashMap<>();

	public Endpoint() {
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		readLock = lock.readLock();
		writeLock = lock.writeLock();
	}
	
	public void registerService(ServiceRegistry... registries) {
		writeLock.lock();
		try {
			for (ServiceRegistry r : registries) {
				for (Integer sid : r.getServiceList()) {
					registryMap.put(sid, r);
				}
			}
		} finally {
			writeLock.unlock();
		}
	}

	public void unregisterService(ServiceRegistry... registries) {
		writeLock.lock();
		try {
			for (ServiceRegistry r : registries) {
				for (Integer sid : r.getServiceList()) {
					registryMap.remove(sid);
				}
			}
		} finally {
			writeLock.unlock();
		}
	}
	
	public ServiceRegistry getRegistry(int serviceId) {
		readLock.lock();
		try {
			return registryMap.get(serviceId);
		} finally {
			readLock.unlock();
		}
	}
	
	public enum RpcStrategy {
		sync,
		async
	}
	
	public enum ControlCommand {
		send_message,
		stop
	}

	public enum RpcState {
		success,
		service_not_exist,
		rpc_canceled,
		service_exception
	}
	
	public interface Callback {
		public void onResponse(GeneratedMessage response); // 正常响应
		public void rpcBeCanceled(); // 调用被取消
		public void serviceNotExist(); // 调用的服务不存在
		public void serviceProcessException(); // 服务处理发生异常
	}
}
