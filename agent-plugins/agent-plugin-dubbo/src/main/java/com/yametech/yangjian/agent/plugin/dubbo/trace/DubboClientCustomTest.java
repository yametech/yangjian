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
package com.yametech.yangjian.agent.plugin.dubbo.trace;

import java.util.HashMap;
import java.util.Map;

import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.custom.IDubboClientCustom;

/**
 * 测试使用
 * @Description 
 * 
 * @author liuzhao
 * @date 2020年4月9日 下午2:15:23
 */
public class DubboClientCustomTest implements IDubboClientCustom {

	@Override
	public boolean filter(Class<?> interfaceCls, String methodName, Class<?>[] parameterTypes) {
		return methodName.equals("hello") && parameterTypes.length == 2;
	}
	
	@Override
	public boolean sample(Object[] obj, ISpanSample configSample) {
		System.err.println("DubboClientCustom >>>>sample");
		return configSample.sample();
	}

	@Override
	public Map<String, String> tags(Object[] obj) {
		Map<String, String> tags = new HashMap<>();
		tags.put("custom", "tags");
		System.err.println("DubboClientCustom >>>>tags");
		if(obj == null || obj.length == 0) {
			return tags;
		}
		for(int i = 0; i < obj.length; i++) {
			tags.put("param-" + i, obj[i].toString());
		}
		return tags;
	}

}
