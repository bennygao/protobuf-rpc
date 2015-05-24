package com.yingshibao.app.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.TextFormat;
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
				TextFormat.printToUnicodeString(userInfo));
		
		RegisterResult.Builder rspBuilder = RegisterResult.newBuilder();
		if (userInfo.getNickName().equalsIgnoreCase("John")) {
			rspBuilder.setErrorMessage("昵称已经存在: John");
		} else {
			rspBuilder.setUserId(userId ++).setErrorMessage("");
			rspBuilder.setSessionId("ABCDEFG0123456789");
		}
		
		push(session);
		
		return rspBuilder.build();
	}
	
	private void push(RpcSession session) {
		Barrage barrage = Barrage.newBuilder().setSenderNickname("卡拉拉" + userId).setMessage("大家好/花").build();
		Push.Client client = new Push.Client(session);
		try {
			logger.info("Server call Client => push");
			client.pushBarrage(barrage, null);
		} catch (Exception e) {
			logger.error("服务器端主动调用客户端失败。", e);
		}
	}
}
