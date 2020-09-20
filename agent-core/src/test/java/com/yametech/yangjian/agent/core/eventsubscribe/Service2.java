package com.yametech.yangjian.agent.core.eventsubscribe;

public class Service2 {
	
	public void test2(String param) {
		System.err.println("Service2.test2 : " + param);
	}
	
	public String test3(String param) {
		System.err.println("Service2.test3 : " + param);
		return "test3 return";
	}
	
}
