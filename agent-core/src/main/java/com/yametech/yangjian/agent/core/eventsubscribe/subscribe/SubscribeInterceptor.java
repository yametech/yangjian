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
package com.yametech.yangjian.agent.core.eventsubscribe.subscribe;

import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.bean.ClassDefined;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.common.MethodUtil;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import com.yametech.yangjian.agent.core.eventsubscribe.base.BindManage;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class SubscribeInterceptor implements IConstructorListener {
	private Map<String, Set<IConfigMatch>> eventGroupMatch = new ConcurrentHashMap<>();
	
	void register(String eventGroup, IConfigMatch methodMatch) {
		Set<IConfigMatch> groupMatch = eventGroupMatch.computeIfAbsent(eventGroup, key -> new CopyOnWriteArraySet<>());
		groupMatch.add(methodMatch);
	}

	@Override
	public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
		ClassDefined classDefined = MethodUtil.getClassDefined(thisObj.getClass());
		for(Method method : thisObj.getClass().getMethods()) {
			MethodDefined methodDefined = MethodUtil.getMethodDefined(classDefined, method);
			for(Entry<String, Set<IConfigMatch>> entry : eventGroupMatch.entrySet()) {
				boolean ignoreParams = entry.getKey().contains(".ignoreParams.");
				entry.getValue().stream()
					.filter(match -> match.isMatch(methodDefined))
					.forEach(match -> BindManage.registerSubscribe(entry.getKey(), ignoreParams, method, thisObj));
			}
		}
	}

}
