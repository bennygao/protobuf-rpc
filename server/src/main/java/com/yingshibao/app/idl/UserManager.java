package com.yingshibao.app.idl;

import com.google.protobuf.nano.MessageNano;

import cc.devfun.pbrpc.ClientStub;
import cc.devfun.pbrpc.Endpoint;
import cc.devfun.pbrpc.RpcSession;
import cc.devfun.pbrpc.ServiceRegistry;

public class UserManager implements ServiceRegistry {
	public interface Impl {
		public RegisterResult registerNewUser(UserInfo userInfo, RpcSession session);
	}

	public interface IFace {
		public RegisterResult registerNewUser(UserInfo userInfo) throws Exception;
		public void registerNewUser(UserInfo userInfo, Endpoint.Callback callback) throws Exception;
	}
	
	public static class Client extends ClientStub implements IFace {
		public Client(RpcSession session) {
			super(session);
		}

		@Override
		public RegisterResult registerNewUser(UserInfo userInfo) throws Exception {
			return (RegisterResult) syncRpc(-2051187760, userInfo); 
		} 
		
		@Override
		public void registerNewUser(UserInfo userInfo, Endpoint.Callback callback) throws Exception {
			asyncRpc(-2051187760, userInfo, callback);
		}
	}

	private Impl serviceImpl;
	
	public UserManager(Impl serviceImpl) {
		this.serviceImpl = serviceImpl;
	}
	
	public UserManager() {
		this(null);
	}

	@Override
	public int[] getServiceList() {
		return new int[] {
			-2051187760,
		};
	}

	@Override
	public boolean hasImplementation() {
		return this.serviceImpl != null;
	}

	@Override
	public MessageNano invokeService(int serviceId, MessageNano arg, RpcSession session) {
		switch (serviceId) {
		case -2051187760:
			return serviceImpl.registerNewUser((UserInfo) arg, session);
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}

	@Override
	public Class<? extends MessageNano> getClassForRequest(int serviceId) {
		switch (serviceId) {
		case -2051187760:
			return UserInfo.class;
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}

	@Override
	public Class<? extends MessageNano> getClassForResponse(int serviceId) {
		switch (serviceId) {
		case -2051187760:
			return RegisterResult.class;
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}
}
