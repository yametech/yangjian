package com.yametech.yangjian.agent.core.eventsubscribe;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.core.report.MultiReport;

import java.lang.reflect.Method;

public class TestSubscribe {
	
	@org.junit.Test
	public void test() {
		System.err.println(1 << 13);
		System.err.println(IConfigReader.class.isAssignableFrom(MultiReport.class));
		for(Method method : Listener.class.getMethods()) {
			System.err.println(method);
			for(Class<?> cls : method.getParameterTypes()) {
				System.err.println(cls.getName());
			}
		}
	}
	
	/**
	 * 测试基本的事件监听，后初始化Listener
	 * 测试结果：正常
	 * @throws InterruptedException 
	 */
	@org.junit.Test
	public void test1() throws InterruptedException {
		Service service = new Service();
		new Listener();
		service.test1();
		service.test2("2222");
		service.test3("3333");
	}
	
	/**
	 * 测试基本的事件监听，先初始化Listener
	 * 测试结果：正常
	 */
	@org.junit.Test
	public void test2() {
		new Listener();
		Service service = new Service();
		service.test1();
		service.test2("2222");
		service.test3("3333");
	}
	
	/**
	 * 测试监听实例在事件产生后注册
	 * 测试结果：之前的事件无法监听，后续产生的可以监听
	 */
	@org.junit.Test
	public void test4() {
		Service service = new Service();
		service.test1();
		service.test2("2222");
		new Listener();
		service.test3("3333");
	}
	
	/**
	 * 测试多个Listener类监听同样的事件
	 * 测试结果：可以正常监听数据
	 */
	@org.junit.Test
	public void test3() {
		Service service = new Service();
		new Listener();
		service.test1();
		new Listener2();
		service.test2("2222");
		service.test3("3333");
	}
	
	/**
	 * 测试一个Listener类监听多个事件
	 * 测试结果：可以正常监听数据
	 */
	@org.junit.Test
	public void test5() {
		Service service = new Service();
		new Listener();
		service.test1();
		new Listener2();
		service.test2("2222");
		Service2 service2 = new Service2();
		service2.test2("aaaa");
		service2.test3("bbbbb");
		
		service.test3("3333");
	}
}
