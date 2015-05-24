package cc.devfun.pbrpc.nio;

import cc.devfun.pbrpc.Message;
import cc.devfun.pbrpc.RpcSession;

import java.io.IOException;

public class NioClientSession implements RpcSession {
	private NioClientEndpoint endpoint;
	
	public NioClientSession(NioClientEndpoint endpoint) {
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
