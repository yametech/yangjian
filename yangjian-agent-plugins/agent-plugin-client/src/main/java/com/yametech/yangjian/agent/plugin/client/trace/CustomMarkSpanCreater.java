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
package com.yametech.yangjian.agent.plugin.client.trace;

import java.lang.reflect.Method;
import java.util.Map;

import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.MicrosClock;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;

import brave.Span;
import brave.Tracer;
import brave.Tracing;

public class CustomMarkSpanCreater implements ISpanCreater<SpanInfo> {
	protected static final MicrosClock MICROS_CLOCK = new MicrosClock();
	protected Tracer tracer;
	private ISpanSample spanSample;
	
	@Override
	public void init(Tracing tracing, ISpanSample spanSample) {
		this.tracer = tracing.tracer();
		this.spanSample = spanSample;
	}
	
	@Override
	public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
		boolean sample = (boolean) allArguments[1];
		if(!sample || !spanSample.sample()) {
			return null;
		}
		long startTime = MICROS_CLOCK.nowMicros();
		if (startTime == -1L) {
			return null;
		}
		Span span = tracer.nextSpan()
				.name(allArguments[0] == null ? "Mark" : allArguments[0].toString())
				.start(startTime);
		@SuppressWarnings("unchecked")
		Map<String, String> tags =  (Map<String, String>) allArguments[2];
		if(tags != null) {
			tags.forEach(span::tag);
		}
		return new BeforeResult<>(null, new SpanInfo(span, tracer.withSpanInScope(span)), null);
	}
	
	@Override
	public Object after(Object thisObj, Object[] allArguments, Method method, Object ret, Throwable t, BeforeResult<SpanInfo> beforeResult) {
		if(beforeResult == null || beforeResult.getLocalVar() == null || beforeResult.getLocalVar().getSpan() == null) {
			return ret;
		}
		SpanInfo span = beforeResult.getLocalVar();
	    if(t != null) {
	    	span.getSpan().error(t);
	    }
		span.getSpan().finish();
		if(span.getScope() != null) {
			span.getScope().close();
		}
		return ret;
	}
	
}
