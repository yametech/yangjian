package com.yametech.yangjian.agent.core.eventsubscribe;

public class Service {
	
	public void test1() {
		System.err.println("Service.test1");
	}
	
	public void test2(String param) {
		System.err.println("Service.test2 : " + param);
	}
	
	public String test3(String param) {
		System.err.println("Service.test3 : " + param);
		if(1 == 1) {
			throw new RuntimeException("测试异常");
		}
		return "test3 return";
	}
	
}
