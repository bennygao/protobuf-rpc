package cc.devfun.pbrpc;

import com.google.protobuf.nano.MessageNano;

public interface ServiceRegistry {
	public int[] getServiceList();
	public boolean hasImplementation();
	public MessageNano invokeService(int serviceId, MessageNano arg, RpcSession session);
	public Class<? extends MessageNano> getClassForRequest(int serviceId);
	public Class<? extends MessageNano> getClassForResponse(int serviceId);
}
