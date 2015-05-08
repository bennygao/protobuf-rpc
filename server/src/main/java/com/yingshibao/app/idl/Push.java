package com.yingshibao.app.idl;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Parser;

import com.yingshibao.foundation.rpc.ClientStub;
import com.yingshibao.foundation.rpc.Endpoint;
import com.yingshibao.foundation.rpc.RpcSession;
import com.yingshibao.foundation.rpc.ServiceRegistry;

public class Push implements ServiceRegistry {
	public interface Impl {
		public None pushBarrage(Barrage barrage, RpcSession session);
	}

	public interface IFace {
		public None pushBarrage(Barrage barrage) throws Exception;
		public void pushBarrage(Barrage barrage, Endpoint.Callback callback) throws Exception;
	}
	
	public static class Client extends ClientStub implements IFace {
		public Client(RpcSession session) {
			super(session);
		}

		@Override
		public None pushBarrage(Barrage barrage) throws Exception {
			return (None) syncRpc(1816601235, barrage); 
		} 
		
		@Override
		public void pushBarrage(Barrage barrage, Endpoint.Callback callback) throws Exception {
			asyncRpc(1816601235, barrage, callback);
		}
	}

	private Impl serviceImpl;
	
	public Push(Impl serviceImpl) {
		this.serviceImpl = serviceImpl;
	}
	
	public Push() {
		this(null);
	}

	@Override
	public int[] getServiceList() {
		return new int[] {
			1816601235,
		};
	}

	@Override
	public GeneratedMessage invokeService(int serviceId, GeneratedMessage arg, RpcSession session) {
		switch (serviceId) {
		case 1816601235:
			return serviceImpl.pushBarrage((Barrage) arg, session);
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}

	@Override
	public Parser<? extends GeneratedMessage> getParserForRequest(int serviceId) {
		switch (serviceId) {
		case 1816601235:
			return Barrage.PARSER;
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}

	@Override
	public Parser<? extends GeneratedMessage> getParserForResponse(int serviceId) {
		switch (serviceId) {
		case 1816601235:
			return None.PARSER;
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}
}
