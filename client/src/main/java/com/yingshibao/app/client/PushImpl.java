package com.yingshibao.app.client;

import cc.devfun.pbrpc.MessageNanoPrinter;
import com.google.protobuf.TextFormat;
import com.yingshibao.app.idl.Barrage;
import com.yingshibao.app.idl.None;
import com.yingshibao.app.idl.Push;
import cc.devfun.pbrpc.RpcSession;

public class PushImpl implements Push.Impl {
	@Override
	public None pushBarrage(Barrage barrage, RpcSession session) {
		System.out.println("#### 弹幕消息：" + MessageNanoPrinter.print(barrage));
		throw new RuntimeException(); // 测试异常信息是否能够通知到调用发起端
//		return null;
	}
}
