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
package com.yametech.yangjian.agent.core.trace;

import java.lang.reflect.Method;
import java.util.Map;

import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.interceptor.IMethodAOP;
import com.yametech.yangjian.agent.api.interceptor.IStaticMethodAOP;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;

import brave.Tracing;

public class TraceAOP<T> implements IMethodAOP<T>, IStaticMethodAOP<T> {
//	private Tracer tracer;
//	private ISpanSample spanSample;
	private ISpanCreater<T> spanCreater;
	
	/**
	 * 
	 * 初始化链路操作实例及span定制逻辑
	 * @param tracing	brave实现的实例
	 * @param spanCustom	span定制实现
	 */
	void init(ISpanCreater<T> spanCreater, Tracing tracing, ISpanSample spanSample) {
		this.spanCreater = spanCreater;
//		this.tracer = tracing.tracer();
//		this.spanSample = spanSample;
		spanCreater.init(tracing, spanSample);
	}

	@Override
	public BeforeResult<T> before(Object[] allArguments, Method method) throws Throwable {
		return spanCreater.before(null, allArguments, method);
	}

	@Override
	public Object after(Object[] allArguments, Method method, BeforeResult<T> beforeResult, Object ret, Throwable t,
			Map<Class<?>, Object> globalVar) throws Throwable {
		spanCreater.after(null, allArguments, method, ret, t, beforeResult);
		return ret;
	}

	@Override
	public BeforeResult<T> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
		return spanCreater.before(thisObj, allArguments, method);
	}

	@Override
	public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult<T> beforeResult,
			Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
		spanCreater.after(thisObj, allArguments, method, ret, t, beforeResult);
		return ret;
	}
	
}
