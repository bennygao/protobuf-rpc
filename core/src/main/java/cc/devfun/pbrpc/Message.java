package cc.devfun.pbrpc;

import com.google.protobuf.nano.MessageNano;

public abstract class Message {
	public final static byte MASK_RESPONSE = 0x01; // 最低位，0表示请求，1表示响应
	public final static byte MASK_SERVICE_NOT_EXIST = 0x02; // 调用的服务未注册
	public final static byte MASK_RPC_CANCELED = 0x04; // 调用被取消（网络连接中断）
	public final static byte MASK_SERVICE_EXCEPTION = 0x08; // 远端处理服务时发生异常

	private int serviceId = 0;
	private int stamp = 0;
	private byte feature = 0;
	private MessageNano argument = null;
	private ResponseHandle responseHandle = null;

	public static Message createMessage(int serviceId, int stamp, byte feature, MessageNano arg) {
		Message message;
		if (isRequest(feature)) {
			message = new RequestMessage(serviceId, stamp, arg);
		} else {
			message = new ResponseMessage(serviceId, stamp, arg);
		}

		message.feature = feature;
		return message;
	}

	public Message(int serviceId, int stamp, MessageNano arg) {
		this.serviceId = serviceId;
		this.stamp = stamp;
		this.argument = arg;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public int getStamp() {
		return stamp;
	}

	public MessageNano getArgument() {
		return argument;
	}

	public byte getFeature() {
		return feature;
	}

	public void setServiceNotExist() {
		feature |= MASK_SERVICE_NOT_EXIST;
	}

	public boolean isServiceNotExist() {
		return (feature & MASK_SERVICE_NOT_EXIST) > 0;
	}

	public void setRpcCanceled() {
		feature |= MASK_RPC_CANCELED;
	}

	public boolean isRpcCanceled() {
		return (feature & MASK_RPC_CANCELED) > 0;
	}

	public void setServiceException() {
		feature |= MASK_SERVICE_EXCEPTION;
	}

	public boolean isServiceException() {
		return (feature & MASK_SERVICE_EXCEPTION) > 0;
	}

	void setToRequest() {
		feature &= ~MASK_RESPONSE;
	}

	void setToResponse() {
		feature |= MASK_RESPONSE;
	}

	public static boolean isRequest(byte feature) {
		return (feature & MASK_RESPONSE) == 0;
	}

	public static boolean isResponse(byte feature) {
		return (feature & MASK_RESPONSE) > 0;
	}

	public abstract boolean isRequest();
	public abstract boolean isResponse();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{serviceId=").append(serviceId).append(", stamp=")
				.append(stamp).append(", feature=").append(binaryString(feature))
				.append(", arg=[");
		if (argument == null) {
			sb.append("null");
		} else {
			sb.append(MessageNanoPrinter.print(argument)).append(']');
		}
		sb.append("}");
		return sb.toString();
	}

	private String binaryString(byte abyte) {
		int mask = 0x0080;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; ++i) {
			if ((abyte & mask) > 0) {
				sb.append('1');
			} else {
				sb.append('0');
			}

			mask >>>= 1;
		}

		return sb.toString();
	}

	public ResponseHandle getResponseHandle() {
		return responseHandle;
	}

	public void setResponseHandle(ResponseHandle responseHandle) {
		this.responseHandle = responseHandle;
	}
}
