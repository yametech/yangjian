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
package com.yametech.yangjian.agent.plugin.mongo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.convert.IMethodConvert;
import com.yametech.yangjian.agent.plugin.mongo.util.MongoUtil;

/**
 * mongo集合操作方法拦截
 *
 * 支持版本：3.8.x-3.12.0
 * com.mongodb.client.internal.MongoClientDelegate$DelegateOperationExecutor.execute(...)
 *
 * @author dengliming
 * @date 2019/12/13
 */
public class DelegateOperationExecutorConvert implements IMethodConvert {
	
	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
		return MongoUtil.buildRTEvent(startTime, allArguments[0]);
	}

}
