package com.yingshibao.app.server;

import cc.devfun.pbrpc.MessagePrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yingshibao.app.idl.Barrage;
import com.yingshibao.app.idl.Push;
import com.yingshibao.app.idl.RegisterResult;
import com.yingshibao.app.idl.UserInfo;
import com.yingshibao.app.idl.UserManager;
import cc.devfun.pbrpc.RpcSession;

public class UserManagerImpl implements UserManager.Impl {
	private static int userId = 1;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public RegisterResult registerNewUser(UserInfo userInfo, RpcSession session) {
		logger.info("call UserManager.registerNewUser -> " +
				MessagePrinter.print(userInfo));
		
		RegisterResult result = new RegisterResult();
		if ("John".equalsIgnoreCase(userInfo.nickName)) {
			result.errorMessage = "昵称已经存在: John";
		} else {
			result.userId = userId ++;
			result.errorMessage = "";
			result.sessionId = "ABCDEFG0123456789";
//			result.sessionId = null;
		}
		
		push(session);
		
		return result;
	}
	
	private void push(RpcSession session) {
		Barrage barrage = new Barrage();
		barrage.senderNickname = "卡拉拉" + userId;
		barrage.message = "大家好/花";
		Push.Client client = new Push.Client(session);
		try {
			logger.info("Server call Client => push");
			client.pushBarrage(barrage, null);
		} catch (Exception e) {
			logger.error("服务器端主动调用客户端失败。", e);
		}
	}
}
