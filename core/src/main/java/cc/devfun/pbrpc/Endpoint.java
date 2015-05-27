package cc.devfun.pbrpc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.protobuf.nano.MessageNano;

public abstract class Endpoint {

	public enum RpcError {
		service_not_exist,
		rpc_canceled,
		service_exception
	}

	public interface Callback {
		public void onResponse(MessageNano response); // 正常响应
		public void onError(RpcError state); // RPC发生错误
	}


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

	public abstract void start() throws Exception;
	public abstract void stop() throws Exception;
}
