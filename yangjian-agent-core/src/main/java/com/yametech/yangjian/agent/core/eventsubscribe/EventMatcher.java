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
package com.yametech.yangjian.agent.core.eventsubscribe;

import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.IMatcherProxy;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.common.MethodUtil;

/**
 * 
 * @Description 
 * 
 * @author liuzhao
 * @date 2020年4月21日 下午3:14:22
 */
public class EventMatcher implements IMatcherProxy<EventDispatcher>, InterceptorMatcher {
//	private static final ILogger LOG = LoggerFactory.getLogger(EventMatcher.class);
	private String eventGroup;
	private IConfigMatch match;
	
	public EventMatcher(String eventGroup, IConfigMatch match) {
		this.eventGroup = eventGroup;
		this.match = match;
	}
	
	@Override
	public void init(EventDispatcher obj, ClassLoader classLoader, MethodType type, MethodDefined methodDefined) {
		obj.init(BindManage.registEvent(eventGroup, methodDefined));
	}

	@Override
	public IConfigMatch match() {
		return match;
	}

	@Override
	public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
		return new LoadClassKey(EventDispatcher.class.getName(), MethodUtil.getId(methodDefined));
	}
	
}
