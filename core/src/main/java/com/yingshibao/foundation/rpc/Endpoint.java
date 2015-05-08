package com.yingshibao.foundation.rpc;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.GeneratedMessage;

public class Endpoint {
	private Map<Integer, ServiceRegistry> registryMap = new HashMap<Integer, ServiceRegistry>();
	
	public void registerService(ServiceRegistry... registries) {
		for (ServiceRegistry r : registries) {
			for (Integer sid : r.getServiceList()) {
				registryMap.put(sid, r);
			}
		}
	}
	
	public ServiceRegistry getRegistry(int serviceId) {
		return registryMap.get(serviceId);
	}
	
	public enum RpcStrategy {
		sync,
		async
	};
	
	public enum ControlCommand {
		send_message,
		stop
	};
	
	public interface Callback {
		public void onResponse(GeneratedMessage response);
	}
}
