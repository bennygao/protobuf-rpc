package com.yingshibao.foundation.rpc;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.TextFormat;

public abstract class Message {
	public final static byte MASK_RESPONSE = (byte) 0X01;
	public final static byte MASK_SERVICE_NOT_EXIST = (byte) 0x02;
	public final static byte MASK_RPC_CANCELED = 0x04;

	private int serviceId = 0;
	private int stamp = 0;
	private byte feature = 0;
	private GeneratedMessage argument = null;
	private ResponseHandle responseHandle = null;

	public static Message createMessage(int serviceId, int stamp, byte feature, GeneratedMessage arg) {
		Message message;
		if (isRequest(feature)) {
			message = new RequestMessage(serviceId, stamp, arg);
		} else {
			message = new ResponseMessage(serviceId, stamp, arg);
		}

		message.feature = feature;
		return message;
	}

	public Message(int serviceId, int stamp, GeneratedMessage arg) {
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

	public GeneratedMessage getArgument() {
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
			sb.append(TextFormat.printToUnicodeString(argument)).append(']');
		}
		sb.append("}");
		return sb.toString();
	}

	private String binaryString(byte abyte) {
		byte mask = (byte) 0x80;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; ++i) {
			if ((abyte & mask) > 0) {
				sb.append('1');
			} else {
				sb.append('0');
			}
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
