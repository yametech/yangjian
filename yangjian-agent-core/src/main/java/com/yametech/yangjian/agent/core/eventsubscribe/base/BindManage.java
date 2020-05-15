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
package com.yametech.yangjian.agent.core.eventsubscribe.base;

import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

public class BindManage {
	private static final ILogger LOG = LoggerFactory.getLogger(BindManage.class);
	private static final int MAX_EVENT = 200;
	private static final int MAX_SUBSCRIBES_EACH_GROUP = 50;
	private static final Map<String, EventSubscribe> events = new ConcurrentHashMap<>();// 事件源方法描述与EventSubscribe的关系
	private static final Map<String, Set<EventSubscribe>> eventSubscribes = new ConcurrentHashMap<>();// 事件分组与EventSubscribe的关系
	private static final Map<String, Map<Method, Map.Entry<Object, Boolean>>> subscribes = new ConcurrentHashMap<>();// 事件分组与订阅方法及实例的关系
	
	private BindManage() {}
	
	/**
	 * 注册事件并返回注册对象，重复注册按照methodDefined判断仅第一次生效
	 * @param methodDefined
	 * @return
	 */
	public static EventSubscribe registerEvent(String eventGroup, MethodDefined methodDefined) {
		LOG.info("registerEvent:{} - {}", eventGroup, methodDefined);
		String methodKey = methodDefined.getMethodDes();
		EventSubscribe eventSubscribe = events.computeIfAbsent(methodKey, key -> {
			if(events.size() > MAX_EVENT) {
				return null;
			}
//			boolean ignoreParams = eventGroup.indexOf(".ignoreParams.") != -1;
			return new EventSubscribe(methodDefined);
		});
		if(eventSubscribe == null) {
			return null;
		}
		Set<EventSubscribe> groupSubscribes = eventSubscribes.computeIfAbsent(eventGroup, key -> new CopyOnWriteArraySet<>());
		groupSubscribes.add(eventSubscribe);
		refreshBind(eventGroup, eventSubscribe);
		return eventSubscribe;
	}
	
	/**
	 * 	注册监听，重复注册按照eventGroup + method判断仅第一次生效
	 * @param eventGroup
	 * @param method
	 * @param instance
	 */
	public static void registerSubscribe(String eventGroup, boolean ignoreParams, Method method, Object instance) {
		LOG.info("registerSubscribe:{} - {} - {}", eventGroup, method, instance);
		Map<Method, Map.Entry<Object, Boolean>> groupSubscribe = subscribes.computeIfAbsent(eventGroup, key -> new ConcurrentHashMap<>());
		AtomicBoolean contains = new AtomicBoolean(true);
		Object value = groupSubscribe.computeIfAbsent(method, key -> {
			if(groupSubscribe.size() > MAX_SUBSCRIBES_EACH_GROUP) {
				LOG.warn("超出最大绑定数量{}：{}", MAX_SUBSCRIBES_EACH_GROUP, method);
				return null;
			}
			contains.set(false);
			return new AbstractMap.SimpleEntry<>(instance, ignoreParams);
		});
		if(value == null || contains.get()) {
			return;
		}
		Set<EventSubscribe> subscribes = eventSubscribes.get(eventGroup);
		if(subscribes != null) {
			subscribes.forEach(eventSubscribe -> refreshBind(eventGroup, eventSubscribe));
		}
	}
	
	/**
	 * 刷新绑定关系
	 * @param eventGroup
	 * @param eventSubscribe
	 */
	private static void refreshBind(String eventGroup, EventSubscribe eventSubscribe) {
		Map<Method, Map.Entry<Object, Boolean>> subscribeMethod = subscribes.get(eventGroup);
		if(subscribeMethod != null && subscribeMethod.size() > 0) {
			subscribeMethod.forEach((key, value) -> eventSubscribe.register(key, value.getKey(), value.getValue()));
		}
	}
	
}
