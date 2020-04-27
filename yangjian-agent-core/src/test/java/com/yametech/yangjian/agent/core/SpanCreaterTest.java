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

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.trace.ICustomLoad;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.custom.IDubboClientCustom;
import com.yametech.yangjian.agent.core.util.Util;

import brave.Tracer;
import brave.Tracing;

public class SpanCreaterTest implements ISpanCreater<Object>, ICustomLoad<IDubboClientCustom> {
	
	@Override
	public void init(Tracing tracing, ISpanSample spanSample) {
		
	}

	@Override
	public BeforeResult<Object> before( Object thisObj, Object[] allArguments, Method method) throws Throwable {
		return null;
	}

	@Override
	public Object after(Object thisObj, Object[] allArguments, Method method, Object ret, Throwable t, BeforeResult<Object> beforeResult) {
		return ret;
	}
	
	@Test
	public void test() {
		Class<?> cls = Util.interfacesGeneric(SpanCreaterTest.class, ICustomLoad.class, 0);
		System.err.println(cls);
		assertEquals("interfacesGeneric error", IDubboClientCustom.class, cls);
	}

	@Override
	public void custom(List<IDubboClientCustom> customInstance) {
		
	}

}
