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
package com.yametech.yangjian.agent.client.test;

import com.yametech.yangjian.agent.client.MetricGroupUtil;
import com.yametech.yangjian.agent.client.MetricUtil;
import org.junit.Test;

public class MetricTest {
	
	@Test
	public void test() throws Exception {
		for(int i = 0; i < 20; i++) {
			MetricUtil.mark("MetricTest", () -> {
				MetricUtil.mark("MetricTest2");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			});
		}
		Thread.sleep(10000);
		System.err.println("关闭服务");
	}

	@Test
	public void testGroup() throws Exception {
		for(int i = 0; i < 20; i++) {
			MetricGroupUtil.mark("test", "key", () -> {
				MetricGroupUtil.mark("test2", "key2", 5);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			});
		}
		Thread.sleep(10000);
		System.err.println("关闭服务");
	}
}
