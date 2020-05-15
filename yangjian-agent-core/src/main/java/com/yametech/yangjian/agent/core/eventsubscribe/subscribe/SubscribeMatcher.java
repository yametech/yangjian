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

import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.IMatcherProxy;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;

/**
 * 
 * @Description 
 * 
 * @author liuzhao
 * @date 2020年4月21日 下午3:14:22
 */
public class SubscribeMatcher implements IMatcherProxy<SubscribeInterceptor>, InterceptorMatcher {
//	private static final ILogger LOG = LoggerFactory.getLogger(EventMatcher.class);
	private String eventGroup;
	private IConfigMatch classMatch;
	private IConfigMatch methodMatch;
	
	public SubscribeMatcher(String eventGroup, IConfigMatch classMatch, IConfigMatch methodMatch) {
		this.eventGroup = eventGroup;
		this.classMatch = classMatch;
		this.methodMatch = methodMatch;
	}
	
	@Override
	public void init(SubscribeInterceptor obj, ClassLoader classLoader, MethodType type, MethodDefined methodDefined) {
		obj.register(eventGroup, methodMatch);
	}

	@Override
	public IConfigMatch match() {
		return classMatch;
	}

	@Override
	public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
		return new LoadClassKey(SubscribeInterceptor.class.getName(), classMatch.toString());
	}
	
}
