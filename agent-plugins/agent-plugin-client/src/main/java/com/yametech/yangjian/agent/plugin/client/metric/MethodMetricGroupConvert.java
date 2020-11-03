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
package com.yametech.yangjian.agent.plugin.client.metric;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.convert.IMethodConvert;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class MethodMetricGroupConvert implements IMethodConvert, IConfigReader {
	private static final int MAX_LENGTH = 50;
	private Set<String> allowGroupNames = new HashSet<>();

	/**
	 * @param kv 配置数据
	 */
	@Override
	public void configKeyValue(Map<String, String> kv) {
		if (kv == null || kv.isEmpty()) {
			allowGroupNames.clear();
		} else {
			allowGroupNames = Arrays.stream(kv.values().iterator().next().split(","))
					.map(String::trim).collect(Collectors.toSet());// 经过测试这个赋值操作不会导致allowGroupNames短暂为null的问题
		}
	}

	@Override
	public Set<String> configKey() {
		return new HashSet<>(Collections.singletonList("metric\\.group\\.allow"));
	}

	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, 
			Method method, Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
		if (allArguments[1] == null) {
			return null;
		}
		String identify = allArguments[1].toString();
		if(identify.length() > MAX_LENGTH) {
			identify = identify.substring(0, MAX_LENGTH);
		}
		TimeEvent event = get(startTime);
		String groupName = allArguments[0] == null ? null : allArguments[0].toString();
		if(groupName != null && allowGroupNames.contains(groupName)) {
			event.setType(groupName);
		}
		event.setIdentify(identify);
		event.setNumber((int)allArguments[2]);
		return Collections.singletonList(event);
	}
}
