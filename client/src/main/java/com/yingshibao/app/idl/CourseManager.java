package com.yingshibao.app.idl;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Parser;

import com.yingshibao.foundation.rpc.ClientStub;
import com.yingshibao.foundation.rpc.Endpoint;
import com.yingshibao.foundation.rpc.RpcSession;
import com.yingshibao.foundation.rpc.ServiceRegistry;

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
	public GeneratedMessage invokeService(int serviceId, GeneratedMessage arg, RpcSession session) {
		switch (serviceId) {
		case -1712929388:
			return serviceImpl.getCourseList((CourseType) arg, session);
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}

	@Override
	public Parser<? extends GeneratedMessage> getParserForRequest(int serviceId) {
		switch (serviceId) {
		case -1712929388:
			return CourseType.PARSER;
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}

	@Override
	public Parser<? extends GeneratedMessage> getParserForResponse(int serviceId) {
		switch (serviceId) {
		case -1712929388:
			return CourseList.PARSER;
		default:
			throw new IllegalArgumentException("not existed service-id: " + serviceId);
		}
	}
}
