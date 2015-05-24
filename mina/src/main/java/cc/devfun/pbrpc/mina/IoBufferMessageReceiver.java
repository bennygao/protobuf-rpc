package cc.devfun.pbrpc.mina;

import cc.devfun.pbrpc.Endpoint;
import cc.devfun.pbrpc.Message;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Parser;
import cc.devfun.pbrpc.ServiceRegistry;

public class IoBufferMessageReceiver {
	public final static String KEY = "RECEIVER";

	enum Phase {
		recv_message_size, recv_message_body
	};

	// message-size字段的长度（字节数）
	public final static int MESSAGE_SIZE_LEN = 4;

	// 最大消息长度，用于验证收到的消息长度是否合法。
	// TODO 根据实际需要修改。
	public final static int MAX_MESSAGE_SIZE = 4096;

	// 当前消息解析状态
	private Phase currentPhase;
	// 当前消息长度
	private int messageSize;
	// 当前绑定的session
	private IoSession session;
	// Endpoint
	private Endpoint endpoint;

	public IoBufferMessageReceiver(IoSession session, Endpoint endpoint) {
		this.session = session;
		currentPhase = Phase.recv_message_size;
		messageSize = 0;
		this.endpoint = endpoint;
	}

	public void clear() {
		currentPhase = Phase.recv_message_size;
		messageSize = 0;
		session = null;
	}

	private Message decodeMessage(IoBuffer buffer) throws Exception {
		// ticket，4个字节int，用以关联同一个service的请求和响应消息。
		int stamp = buffer.getInt();
		
		// serviceId, 4个字节，用以标识调用/响应的service。
		int serviceId = buffer.getInt();
		
		// phase，1个字节，用以标识消息是请求还是响应。
		byte feature = buffer.get();

		
		int oldLimit = buffer.limit();
		int need = messageSize - 9;
		if (buffer.remaining() > need) {
			int limit = buffer.position() + need;
			buffer.limit(limit);
		}

		// protobuf格式的参数
		GeneratedMessage protobuf = null;
		Message message = null;
		Parser<? extends GeneratedMessage> parser = null;
		ServiceRegistry registry = endpoint.getRegistry(serviceId);
		if (registry == null) { // 请求的服务未注册
			message = Message.createMessage(serviceId, stamp, feature, null);
			message.setServiceNotExist();

			// 略去protobuf数据
			buffer.position(buffer.position() + need);
		} else {
			if (Message.isRequest(feature)) {
				parser = registry.getParserForRequest(serviceId);
			} else {
				parser = registry.getParserForResponse(serviceId);
			}

			if (need > 0) {
				protobuf = parser.parseFrom(buffer.asInputStream());
			}

			message = Message.createMessage(serviceId, stamp, feature, protobuf);
		}

		buffer.limit(oldLimit);
		return message;
	}

	public Message onDataArrived(IoBuffer buffer) throws Exception {
		if (currentPhase == Phase.recv_message_body) {
			if (buffer.remaining() < messageSize) {
				return null;
			} else {
				try {
					return decodeMessage(buffer);
				} finally {
					currentPhase = Phase.recv_message_size;
				}
			}
		} else if (currentPhase == Phase.recv_message_size) {
			if (buffer.remaining() < MESSAGE_SIZE_LEN) {
				return null;
			} else {
				messageSize = buffer.getInt();
				if (messageSize <= 0) {
					throw new Exception(String.format(
							"Suspicious message size %d came from %s.",
							messageSize, session.getRemoteAddress().toString()));
				} else if (messageSize > MAX_MESSAGE_SIZE) {
					throw new Exception(String.format(
							"Message size %d exceeds limit %d came from %s.",
							messageSize, MAX_MESSAGE_SIZE, session
									.getRemoteAddress().toString()));
				} else {
					try {
						currentPhase = Phase.recv_message_body;
						return onDataArrived(buffer);
					} catch (Throwable t) {
						throw new Exception(String.format(
								"deserialize message came from %s failed.",
								session.getRemoteAddress().toString()), t);
					}
				}
			}
		} else {
			throw new IllegalStateException("Error message receive phase "
					+ currentPhase);
		}
	}
}
