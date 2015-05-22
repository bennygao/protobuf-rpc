package com.yingshibao.foundation.rpc;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Parser;

public interface ServiceRegistry {
	public int[] getServiceList();
	public boolean hasImplementation();
	public GeneratedMessage invokeService(int serviceId, GeneratedMessage arg, RpcSession session);
	public Parser<? extends GeneratedMessage> getParserForRequest(int serviceId);
	public Parser<? extends GeneratedMessage> getParserForResponse(int serviceId);
}
