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
package com.yametech.yangjian.agent.plugin.dubbo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.yametech.yangjian.agent.plugin.dubbo.base.RpcClient;

public class Main {
	
	/**
	 * 测试javaagent
	 * 		-javaagent:E:\eclipse-workspace\tool-ecpark-monitor\ecpark-agent\target\ecpark-agent.jar
	 * 
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
//		try {
		test();
//		testDubboClient();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		Thread.sleep(5000);
		System.exit(0);
	}
	
	private static void test() {
		DubboTest.initDubboServer("1.5.0");
		DubboTest.initDubboClient("1.5.0");
		IDubboService service = RpcClient.getService(IDubboService.class);
		System.err.println(service.hello("aaa"));
	}
	
	private static void testDubboClient() throws InterruptedException {
//		DubboTest.initDubboServer();
		DubboTest.initDubboClient();
		IDubboService service = RpcClient.getService(IDubboService.class);
		for(int i = 0; i < 100000; i++) {
			service.hello("aaa");
		}
		
		// 不加逻辑跑dubbo测试时会有较大的性能损耗（20% - 50%），如果方法包含了处理逻辑（测试使用LockSupport.parkNanos休眠即纳秒）几乎无性能损耗，很有可能因资源竞争较大导致
		long start = System.currentTimeMillis();
		ExecutorService executor = Executors.newFixedThreadPool(3);
		for(int i = 0; i < 3; i++) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					for(int i = 0; i < 1000000; i++) {
						service.hello("aaa");
					}
				}
			});
		}
//		executor.shutdown();
//		executor.awaitTermination(1, TimeUnit.DAYS);
		// 单线程发布
		//	1000000		100000		100000	1000000
		//	3047		802			252		2241
		//	2965		687			257		2213
		//	2940		738			256		2318
		//	2859		782			252		2141
		// 	2788		759			287		2362
		//	3095		683			299		2194
		//	2981		693			261		2185
		// avg=2953		avg=		avg=	avg=2236
		
		//	1000000		100000		100000	1000000
		//	2159		343			205		1849
		//	2204		330			214		1671
		//	2183		322			218		1912
		//	2085		349			211		1862
		//	2243		351			204		1736
		//	2066		344			201		1653
		//									1782
		//	avg=2156	avg=		avg=	avg=1780
		
		// 多线程发布（10个线程1000000）
		//	关闭		开启
		//	593		743
		//	631		1159
		//	607		1118
		//	657		1097
		//	642		
		//	592
		//	
		//	
		System.err.println(System.currentTimeMillis() - start);// 5729
//		System.err.println(service.hello("aaa"));
	}
	
}
