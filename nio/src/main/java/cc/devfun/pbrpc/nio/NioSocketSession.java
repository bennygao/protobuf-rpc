package cc.devfun.pbrpc.nio;

import cc.devfun.pbrpc.Message;
import cc.devfun.pbrpc.RpcSession;

import java.io.IOException;

public class NioSocketSession implements RpcSession {
	private NioSocketEndpoint endpoint;
	
	public NioSocketSession(NioSocketEndpoint endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public void sendMessage(Message message) {
		try {
			endpoint.sendMessage(message);
		} catch (IOException ioe) {
			System.err.println("send message error." + message);
			ioe.printStackTrace();
		}
		
	}

}
