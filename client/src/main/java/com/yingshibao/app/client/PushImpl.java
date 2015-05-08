package com.yingshibao.app.client;

import org.slf4j.LoggerFactory;

import com.google.protobuf.TextFormat;
import com.yingshibao.app.idl.Barrage;
import com.yingshibao.app.idl.None;
import com.yingshibao.app.idl.Push;
import com.yingshibao.foundation.rpc.RpcSession;

public class PushImpl implements Push.Impl {
	@Override
	public None pushBarrage(Barrage barrage, RpcSession session) {
		LoggerFactory.getLogger(getClass()).info("弹幕消息：" + TextFormat.printToUnicodeString(barrage));
		return null;
	}

}
