package com.yametech.yangjian.agent.core.eventsubscribe;

public class Listener2 {
	
	public void test2(String param) {
		System.err.println("Listener2.test2 : " + param);
	}
	
	public void test3_1(String param, Service service, Throwable t) {
		System.err.println("Listener2.test3_1 : " + param + "	" + service + "	" + t);
	}
}
