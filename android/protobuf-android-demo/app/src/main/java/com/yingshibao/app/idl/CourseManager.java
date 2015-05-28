package com.yingshibao.app.idl;

import com.google.protobuf.nano.MessageNano;

import cc.devfun.pbrpc.ClientStub;
import cc.devfun.pbrpc.Endpoint;
import cc.devfun.pbrpc.RpcSession;
import cc.devfun.pbrpc.ServiceRegistry;

public class CourseManager implements ServiceRegistry {
	public interface Impl {
		public CourseList getCourseList(CourseType courseType, RpcSession session);
	}

	public interface IFace {
		public CourseList getCourseList(CourseType courseType) throws Exception;
		public void getCourseList(CourseType courseType, Endpoint.Callback callback) throws Exception;
	}
	
	public static class Client extends ClientStub implements IFace {
		public Client(RpcSession session) {
			super(session);
		}

		@Override
		public CourseList getCourseList(CourseType courseType) throws Exception {
			return (CourseList) syncRpc(-1712929388, courseType); 
		} 
		
		@Override
		public void getCourseList(CourseType courseType, Endpoint.Callback callback) throws Exception {
			asyncRpc(-1712929388, courseType, callback);
		}
	}

	private Impl serviceImpl;
	
	public CourseManager(Impl serviceImpl) {
		this.serviceImpl = serviceImpl;
	}
	
	public CourseManager() {
		this(null);
	}

	@Override
	public int[] getServiceList() {
		return new int[] {
			-1712929388,
		};
	}

	@Override
	public boolean hasImplementation() {
		return this.serviceImpl != null;
	}

	@Override
	public MessageNano invokeService(int serviceId, MessageNano arg, RpcSession session) {
		switch (serviceId) {
		case -1712929388:
			return serviceImpl.getCourseList((CourseType) arg, session);
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}

	@Override
	public Class<? extends MessageNano> getClassForRequest(int serviceId) {
		switch (serviceId) {
		case -1712929388:
			return CourseType.class;
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}

	@Override
	public Class<? extends MessageNano> getClassForResponse(int serviceId) {
		switch (serviceId) {
		case -1712929388:
			return CourseList.class;
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}
}
