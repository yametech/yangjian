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

import java.util.Arrays;

import com.yametech.yangjian.agent.api.IEnhanceClassMatch;
import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.configmatch.ClassMatch;
import com.yametech.yangjian.agent.api.configmatch.CombineAndMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodArgumentIndexMatch;

/**
 * 增强类定义
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月8日 下午6:13:04
 */
public class ConsumeAddressMatcher implements InterceptorMatcher, IEnhanceClassMatch {
	
	@Override
	public IConfigMatch classMatch() {
		return new ClassMatch("org.apache.kafka.clients.consumer.KafkaConsumer");
	}
	
	@Override
	public IConfigMatch match() {
		return new CombineAndMatch(Arrays.asList(
				new ClassMatch("org.apache.kafka.clients.consumer.KafkaConsumer"),
//				new MethodNameMatch("KafkaPublisher"),
//				new MethodArgumentNumMatch(1),
				new MethodArgumentIndexMatch(0, "java.util.Properties")
			));
	}
	
	@Override
	public LoadClassKey loadClass(MethodType type) {
		return new LoadClassKey("com.yametech.yangjian.agent.plugin.kafka.context.ConsumeAddressInterceptor");
	}
	
}
