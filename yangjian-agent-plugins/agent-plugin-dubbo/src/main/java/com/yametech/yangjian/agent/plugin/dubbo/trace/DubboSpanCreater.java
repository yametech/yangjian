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
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.trace.ICustomLoad;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanCustom;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.custom.IDubboCustom;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.internal.Platform;

public abstract class DubboSpanCreater<T extends ISpanCustom> implements ISpanCreater<SpanInfo>, ICustomLoad<T> {
	private List<IDubboCustom> customs;
	protected Tracer tracer;
	private ISpanSample spanSample;
	
	@Override
	public void init(Tracing tracing, ISpanSample spanSample) {
		this.tracer = tracing.tracer();
		this.spanSample = spanSample;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void custom(List<T> customs) {
		this.customs = (List<IDubboCustom>) customs;
	}
	
	protected BeforeResult<SpanInfo> spanInit(Span span, Object[] allArguments, IDubboCustom custom) {
		InetSocketAddress remoteAddress = RpcContext.getContext().getRemoteAddress();
	    if (remoteAddress != null) {
	    	span.remoteIpAndPort(Platform.get().getHostString(remoteAddress), remoteAddress.getPort());
	    }
	    setTags(span, allArguments, custom);
	    return new BeforeResult<>(null, new SpanInfo(span, tracer.withSpanInScope(span)), null);
	}
	
	@Override
	public void after(Object thisObj, Object[] allArguments, Method method, Object ret, Throwable t, BeforeResult<SpanInfo> beforeResult) {
		if(beforeResult == null || beforeResult.getLocalVar() == null || beforeResult.getLocalVar().getSpan() == null) {
			return;
		}
		SpanInfo span = beforeResult.getLocalVar();
	    Throwable exception = t;
	    if (exception == null) {
	    	Result result = (Result) ret;
	    	if (result.hasException()) {
	    		exception = result.getException();
	    	}
	    }
	    if(exception != null) {
	    	span.getSpan().error(exception);
			if (exception instanceof RpcException) {
				span.getSpan().tag("dubbo.error_code", Integer.toString(((RpcException) exception).getCode()));
			}
	    }
		span.getSpan().finish();
		if(span.getScope() != null) {
			span.getScope().close();
		}
	}
	
	/**
	 * 获取匹配的custom，如果有多个使用第一个
	 * @param interfaceCls
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	protected IDubboCustom getCustom(Class<?> interfaceCls, String methodName, Class<?>[] parameterTypes) {
		if(customs == null) {
			return null;
		}
		for(IDubboCustom custom : customs) {
			if(custom.filter(interfaceCls, methodName, parameterTypes)) {
				return custom;
			}
		}
		return null;
	}

	/**
	 * 是否生成Span
	 * @param allArguments
	 * @return
	 */
	protected boolean generateSpan(Object[] allArguments, IDubboCustom custom) {
		if(custom != null) {
			return custom.sample(allArguments, spanSample);
		}
		return spanSample.sample();
	}
	
	/**
	 * 获取span名称
	 * @param className
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	protected String getSpanName(String className, String methodName, Class<?>[] parameterTypes) {
		StringBuilder name = new StringBuilder();
		name.append(className)
			.append('.').append(methodName)
			.append('(');
		for (Class<?> classes : parameterTypes) {
			name.append(classes.getSimpleName() + ",");
        }
        if (parameterTypes.length > 0) {
        	name.delete(name.length() - 1, name.length());
        }
        name.append(")");
        return name.toString();
	}
	
	/**
	 * 设置tags
	 * @param span
	 * @param arguments
	 */
	protected void setTags(Span span, Object[] arguments, IDubboCustom custom) {
		if(custom == null) {
			return;
		}
		Map<String, String> tags = custom.tags(arguments);
		if(tags != null) {
			tags.forEach(span::tag);
		}
	}
	
}
