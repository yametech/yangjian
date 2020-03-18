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
package com.yametech.yangjian.agent.plugin.kafka.context;

import java.util.Properties;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import com.yametech.yangjian.agent.plugin.kafka.bean.MqInfo;

/**
 * 增强类定义
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月8日 下午6:13:04
 */
public class PublishAddressInterceptor implements IConstructorListener {
	
	@Override
	public void constructor(Object thisObj, Object[] allArguments) {
		if(allArguments == null || !(thisObj instanceof IContext) || allArguments[0] == null) {
			return;
		}
		Properties prop = (Properties) allArguments[0];
		MqInfo info = new MqInfo(prop.getProperty("bootstrap.servers"), null, null);
		((IContext)thisObj)._setAgentContext(ContextConstants.KAFKA_CONTEXT_KEY, info);
	}
}
