/*
 * Copyright 2020 yametech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yametech.yangjian.agent.core;

import com.yametech.yangjian.agent.core.inter.TestService;

public class Main {
	
	/**
	 * 测试javaagent
	 * 		-javaagent:E:\eclipse-workspace\tool-ecpark-monitor\ecpark-agent\target\ecpark-agent.jar
	 * 
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
//		java.time.Duration.ofHours(12);
//		BlockingQueue queue = new ArrayBlockingQueue<>(10);
//		queue.add("123");
//		
//		ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 8, 10000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10));
//		executor.execute(() -> {
//			System.err.println("execute");
//		});
		testTime();
		Thread.sleep(3000);
		
//		new IterClass().print();
		
		System.exit(0);
	}
	
	private static void testTime() {
		TestService test = new TestService();
		long start = System.currentTimeMillis();
		for(int i = 0; i < 10; i++) {
//			test.m1();
//			test.m2("test");
			test.add(1, 2);
			TestService.multiply(2, 3);
		}
		System.err.println(System.currentTimeMillis() - start);
//		test.toString();
	}
}
