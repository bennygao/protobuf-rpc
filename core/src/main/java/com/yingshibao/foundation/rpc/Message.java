package com.yingshibao.foundation.rpc;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.TextFormat;

public class Message {
	public enum Type {
		application, // 应用消息
		cancel // 取消等待
	};
	
	public final static byte STAGE_UNKNOWN = -1;
	public final static byte STAGE_REQUEST = 0;
	public final static byte STAGE_RESPONSE = 1;

	private Type type = Type.application;
	private int serviceId = 0;
	private int stamp = 0;
	private byte stage = STAGE_UNKNOWN;
	private GeneratedMessage argument = null;
	private ResponseHandle responseHandle = null;
	
	public Message(Type type) {
		this.type = type;
	}
	
	public Message(int serviceId) {
		this.type = Type.application;
		this.serviceId = serviceId;
	}
	
	public Message(int serviceId, int stamp, byte stage, GeneratedMessage arg) {
		this.type = Type.application;
		this.serviceId = serviceId;
		this.stamp = stamp;
		this.stage = stage;
		this.argument = arg;
	}
	
	public Type getType() {
		return type;
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

	public void setStamp(int stamp) {
		this.stamp = stamp;
	}

	public byte getStage() {
		return stage;
	}

	public void setStage(byte stage) {
		this.stage = stage;
	}

	public GeneratedMessage getArgument() {
		return argument;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{serviceId=").append(serviceId).append(", stamp=")
				.append(stamp).append(", stage=").append(stage)
				.append(", arg=[");
		if (argument == null) {
			sb.append("null");
		} else {
			sb.append(TextFormat.printToUnicodeString(argument)).append(']');
		}
		sb.append("}");
		return sb.toString();
	}

	public ResponseHandle getResponseHandle() {
		return responseHandle;
	}

	public void setResponseHandle(ResponseHandle responseHandle) {
		this.responseHandle = responseHandle;
	}
}
