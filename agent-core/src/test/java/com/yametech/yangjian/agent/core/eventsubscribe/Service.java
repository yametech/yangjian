package com.yametech.yangjian.agent.core.eventsubscribe;

import com.yametech.yangjian.agent.client.EventSubscribeParam;

public class Service {
	
	public void test1() {
		EventSubscribeParam.set("key1", "value1");
		EventSubscribeParam.set("key2", "value2");
		System.err.println("call com.yametech.yangjian.agent.core.eventsubscribe.Service.test1");
	}
	
	public void test2(String param) {
		System.err.println("call com.yametech.yangjian.agent.core.eventsubscribe.Service.test2 : " + param);
	}
	
	public String test3(String param) {
		System.err.println("call com.yametech.yangjian.agent.core.eventsubscribe.Service.test3 : " + param);
		if(1 == 1) {
			throw new RuntimeException("测试异常");
		}
		return "test3 return";
	}
	
}
