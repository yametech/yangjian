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
package com.yametech.yangjian.agent.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.yametech.yangjian.agent.api.base.IMatcherProxy;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.trace.ITraceMatcher;
import com.yametech.yangjian.agent.core.common.BaseEventPublish;
import com.yametech.yangjian.agent.core.util.Util;
import com.yametech.yangjian.agent.util.eventbus.consume.ConsumeConfig;

public class MatcherProxyTest extends BaseEventPublish<String> implements IMatcherProxy<String> {
	
	public MatcherProxyTest() {
		super(null);
	}
	
	@Test
	public void test() {
//		assertTrue("superClassGeneric error", String.class.equals(Util.superClassGeneric(MatcherProxyTest.class, 0)));
//		assertTrue("interfacesGeneric error", ITraceMatcher.class.equals(Util.interfacesGeneric(MatcherProxyTest.class, IMatcherProxy.class, 1)));
		
		assertEquals("superClassGeneric error", String.class, Util.superClassGeneric(MatcherProxyTest.class, 0));
		assertEquals("interfacesGeneric error", ITraceMatcher.class, Util.interfacesGeneric(MatcherProxyTest.class, IMatcherProxy.class, 1));
	}

	@Override
	public void init(String obj, ClassLoader classLoader, MethodType type, MethodDefined methodDefined) {
	}

	@Override
	protected List<ConsumeConfig<String>> consumes() {
		return null;
	}
	
}
