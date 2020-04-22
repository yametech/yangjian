package com.yametech.yangjian.agent.core.eventsubscribe;

public class Listener {
	public void test1() throws InterruptedException {
		System.err.println("Listener.test1");
//		Thread.sleep(10000);
	}
	
	public void test1_1(Service service, Throwable t) throws InterruptedException {
		System.err.println("Listener.test1_1 ： " + service + "	" + t);
//		Thread.sleep(10000);
	}
	
	public void test1_2(Throwable t) throws InterruptedException {
		System.err.println("Listener.test1_2");
//		Thread.sleep(10000);
	}
	
	public void test2(String param) throws InterruptedException {
		System.err.println("Listener.test2 : " + param);
//		Thread.sleep(10000);
	}
	
	public void test2_1(String param, Service service, Throwable t) throws InterruptedException {
		System.err.println("Listener.test2_1 : " + param + "	" + service + "	" + t);
//		Thread.sleep(10000);
	}
	
	public void test3(String param) throws InterruptedException {
		System.err.println("Listener.test3 : " + param);
//		Thread.sleep(10000);
	}
	
	public void test3_1(String param, Service service, Throwable t) throws InterruptedException {
		System.err.println("Listener.test3_1 : " + param + "	" + service + "	" + t);
//		Thread.sleep(10000);
		// 模拟死循环
		service.test3("qqqqqq");
	}
}
