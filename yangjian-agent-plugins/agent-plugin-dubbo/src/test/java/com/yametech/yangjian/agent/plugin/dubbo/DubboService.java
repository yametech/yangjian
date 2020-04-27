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

import java.util.HashMap;
import java.util.Map;

import com.yametech.yangjian.agent.client.TraceUtil;

public class DubboService implements IDubboService {

	@Override
	public String hello(String name) {
//		LockSupport.parkNanos(new Random().nextInt() % 3);
		System.err.println("call hello");
		return "hello " + name;
	}
	
	@Override
	public String hello(String name, Integer age) {
		System.err.println("call hello age");
		Map<String, String> tags = new HashMap<>();
		tags.put("tag-1", "value-1");
		tags.put("tag-2", "value-2");
		tags.put("tag-3", "value-3");
		TraceUtil.mark("test-name", tags, () -> {
			System.err.println("mark start");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.err.println("mark end");
			return null;
		});
		return "hello " + name + " - " + age;
	}
	
	@Override
	public String heart() {
		System.err.println("call heart");
		return "heart";
	}
	
}
