package com.yingshibao.app.idl;

import com.google.protobuf.nano.MessageNano;

import cc.devfun.pbrpc.ClientStub;
import cc.devfun.pbrpc.Endpoint;
import cc.devfun.pbrpc.RpcSession;
import cc.devfun.pbrpc.ServiceRegistry;

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
	public boolean hasImplementation() {
		return this.serviceImpl != null;
	}

	@Override
	public MessageNano invokeService(int serviceId, MessageNano arg, RpcSession session) {
		switch (serviceId) {
		case 1816601235:
			return serviceImpl.pushBarrage((Barrage) arg, session);
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}

	@Override
	public Class<? extends MessageNano> getClassForRequest(int serviceId) {
		switch (serviceId) {
		case 1816601235:
			return Barrage.class;
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}

	@Override
	public Class<? extends MessageNano> getClassForResponse(int serviceId) {
		switch (serviceId) {
		case 1816601235:
			return None.class;
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}
}
