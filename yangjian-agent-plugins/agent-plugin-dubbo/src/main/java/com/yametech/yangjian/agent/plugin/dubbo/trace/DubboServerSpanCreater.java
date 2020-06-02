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
package com.yametech.yangjian.agent.plugin.dubbo.trace;

import java.lang.reflect.Method;
import java.util.Map;

import brave.propagation.ExtraFieldPropagation;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.StringUtil;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcContext;

import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.BraveUtil;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import com.yametech.yangjian.agent.api.trace.custom.IDubboCustom;
import com.yametech.yangjian.agent.api.trace.custom.IDubboServerCustom;

import brave.Span;
import brave.Span.Kind;
import brave.Tracing;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;

public class DubboServerSpanCreater extends DubboSpanCreater<IDubboServerCustom> {
	private TraceContext.Extractor<Map<String, String>> extractor;
	
	@Override
	public void init(Tracing tracing, ISpanSample spanSample) {
		super.init(tracing, spanSample);
		this.extractor = tracing.propagation().extractor(BraveUtil.MAP_GETTER);
	}

	@Override
	public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
		RpcContext rpcContext = RpcContext.getContext();
		Kind kind = rpcContext.isConsumerSide() ? Kind.CLIENT : Kind.SERVER;
		if(!kind.equals(Kind.SERVER)) {
			return null;
		}
		Invoker<?> invoker = (Invoker<?>) allArguments[0];
		Invocation invocation = (Invocation) allArguments[1];
		IDubboCustom custom = getCustom(invoker.getInterface(), invocation.getMethodName(), invocation.getParameterTypes());
		if(!generateSpan(invocation.getArguments(), custom)) {// 不需要生成
			return null;
		}
		long startTime = MICROS_CLOCK.nowMicros();
		if (startTime == -1L) {
			return null;
		}
		TraceContextOrSamplingFlags extracted = extractor.extract(invocation.getAttachments());// 注入请求中带的链路信息
//		invoker.getUrl().getServiceInterface();
//		invoker.getInterface().getName();
		Span span = tracer.nextSpan(extracted)
				.kind(kind)
				.name(getSpanName(invoker.getInterface().getName(), invocation.getMethodName(), invocation.getParameterTypes()))
				.start(startTime);
		String parentServiceName = ExtraFieldPropagation.get(span.context(), Constants.ExtraHeaderKey.REFERER_SERVICE);
		if (StringUtil.notEmpty(span.context().parentIdString()) && StringUtil.notEmpty(parentServiceName)) {
			span.tag(Constants.Tags.PARENT_SERVICE_NAME, parentServiceName);
		}
		return spanInit(span, invocation.getArguments(), custom);
	}
}
