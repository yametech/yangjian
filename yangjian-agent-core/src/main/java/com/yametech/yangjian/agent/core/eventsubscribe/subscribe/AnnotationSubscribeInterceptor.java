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

import com.yametech.yangjian.agent.api.common.MethodUtil;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.client.annotation.IgnoreParams;
import com.yametech.yangjian.agent.client.annotation.Subscribe;
import com.yametech.yangjian.agent.core.eventsubscribe.base.BindManage;

import java.lang.reflect.Method;

/**
 * 
 * @Description
 * 
 * @author liuzhao
 * @date 2020年5月13日 下午5:13:31
 */
public class AnnotationSubscribeInterceptor implements IConstructorListener {
	private static final ILogger LOG = LoggerFactory.getLogger(AnnotationSubscribeInterceptor.class);
	
	@Override
	public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
		for(Method method : thisObj.getClass().getMethods()) {
			String eventGroup = MethodUtil.getId(method);
			IgnoreParams ignoreParams = method.getAnnotation(IgnoreParams.class);
			Subscribe[] eventsConfig = method.getAnnotationsByType(Subscribe.class);
			for(Subscribe subscribe : eventsConfig) {
				BindManage.registerSubscribe(eventGroup, ignoreParams != null, method, thisObj);
			}
		}
	}
	
}
