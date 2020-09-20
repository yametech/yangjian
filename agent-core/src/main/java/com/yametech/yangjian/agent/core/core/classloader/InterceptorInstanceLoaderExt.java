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


package com.yametech.yangjian.agent.core.core.classloader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yametech.yangjian.agent.core.util.Value;

public class InterceptorInstanceLoaderExt {
	private static Map<String, Object> convertAopInstance = new ConcurrentHashMap<>();
	
	private InterceptorInstanceLoaderExt() {}
	
	/**
	 * 加载类
	 * @param key
	 * @param className
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(String key, String className) throws Exception {
		Class<?> keyCls = Class.forName(className);
		Class<?> instanceCls = keyCls;
		Value<Exception> value = Value.absent();
		Object instance = convertAopInstance.computeIfAbsent(key, cls -> {
			try {
				return instanceCls.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				value.set(e);
				return null;
			}
		});
		if(value.get() != null) {
			throw value.get();
		}
		return (T) instance;
	}
}
