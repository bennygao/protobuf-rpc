package com.yingshibao.foundation.rpc.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.google.protobuf.GeneratedMessage;
import com.yingshibao.foundation.rpc.Message;

/**
 * 消息编码器工厂
 */
public class MessageCodecFactory implements ProtocolCodecFactory {
	// 发送消息时封装数据
	private final ProtocolEncoder encoder;
	// 收到数据时解析消息
	private final ProtocolDecoder decoder;

	public MessageCodecFactory() {
		encoder = new MessageEncoder();
		decoder = new MessageDecoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return encoder;
	}
}

/**
 * 消息编码器
 */
class MessageEncoder implements ProtocolEncoder {	
	public void encode(IoSession session, Object obj,
			ProtocolEncoderOutput out) throws Exception {
		Message message = (Message) obj;
		IoBuffer buffer = IoBuffer.allocate(4096);
		int messageSize = 9;
		GeneratedMessage arg = message.getArgument();
		if (arg != null) {
			messageSize += arg.getSerializedSize();
		}
		buffer.putInt(messageSize);
		buffer.putInt(message.getStamp());
		buffer.putInt(message.getServiceId());
		buffer.put(message.getFeature());
		if (arg != null) {
			arg.writeTo(buffer.asOutputStream());
		}
		buffer.flip();
		out.write(buffer);
	}

	public void dispose(IoSession session) throws Exception {
		// nothing to dispose
	}
}

/**
 * 消息解码器
 */
class MessageDecoder extends CumulativeProtocolDecoder {
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		IoBufferMessageReceiver receiver = (IoBufferMessageReceiver) session
				.getAttribute(IoBufferMessageReceiver.KEY);
		if (receiver == null) {
			// Sessions上还未绑定Receiver，等待
			return false;
		}

		Message message = receiver.onDataArrived(in);
		if (message != null) {
			out.write(message);
			return true;
		} else {
			return false;
		}
	}
}
