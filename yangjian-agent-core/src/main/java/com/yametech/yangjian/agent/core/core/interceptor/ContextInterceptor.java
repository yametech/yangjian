/**
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

package com.yametech.yangjian.agent.core.core.interceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yametech.yangjian.agent.core.core.agent.IContextField;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * 方法名字一定不能改
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月14日 下午11:45:29
 */
public class ContextInterceptor {
	
	@RuntimeType
	public static Object _getAgentContext(String key, @This IContextField thisObj) {
		if(thisObj == null || thisObj.__getAgentContext() == null) {
    		return null;
    	}
    	return thisObj.__getAgentContext().get(key);
	}

	@RuntimeType
	public static void _setAgentContext(String key, Object value, @This IContextField thisObj) {
		if(thisObj == null) {
    		return;
    	}
		Map<String, Object> data = thisObj.__getAgentContext();
		if(data == null) {
			data = new ConcurrentHashMap<>();
			thisObj.__setAgentContext(data);
		}
		data.put(key, value);
	}

}
