package com.cloud.push.test;

import org.junit.Test;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.model.PushPayload;

public class JpushTest {

	@Test
	public void fun1() {
		try {
			JPushClient jPushClient = new JPushClient("7b86cdd203328e704718bce9", "bd5ea486e324b235c89b6340");
			PushPayload pushPayload = PushPayload.alertAll("");
			jPushClient.sendPush(pushPayload);
			//如果使用 NettyHttpClient(v3.2.15 版本新增），需要在响应返回后手动调用一下 NettyHttpClient 中的 close 方法，否则进程不会退出
			jPushClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
