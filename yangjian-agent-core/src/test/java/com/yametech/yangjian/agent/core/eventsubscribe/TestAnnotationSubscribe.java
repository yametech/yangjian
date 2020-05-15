package com.yametech.yangjian.agent.core.eventsubscribe;

import com.yametech.yangjian.agent.client.annotation.Register;

import java.util.concurrent.Executors;

@Register({AnnotationListener.class})
public class TestAnnotationSubscribe {

	/**
	 * 测试基本的事件监听，后初始化Listener
	 * 测试结果：正常
	 * @throws InterruptedException 
	 */
	@org.junit.Test
	public void test1() throws InterruptedException {
		Thread.sleep(2000);
//		new AnnotationListener();

		Service service = new Service();
//		new Listener();
		service.test1();
		service.test2("2222");
		new AnnotationListener().self();
		service.test3("3333");
		Thread.sleep(2000);
	}

	@org.junit.Test
	public void test2() {
		System.err.println(Thread.currentThread().getName());
		Executors.newSingleThreadExecutor().submit(() -> System.err.println(Thread.currentThread().getName()));
		Executors.newSingleThreadExecutor().execute(() -> System.err.println(Thread.currentThread().getName()));
	}
}
