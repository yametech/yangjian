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
package com.yametech.yangjian.agent.api.trace;

import java.lang.reflect.Method;

import com.yametech.yangjian.agent.api.bean.BeforeResult;

import brave.Tracing;

/**
 * 创建链路span
 * 
 * @author liuzhao
 */
public interface ISpanCreater<T> {
	
	/**
	 * @param tracing	生成Span的brave链路实例
	 * @param spanSample	span采样率配置，不为null
	 */
	void init(Tracing tracing, ISpanSample spanSample);
	
	/**
	 * 
	 * @param thisObj	拦截的方法类实例，如果拦截的是静态方法，则该值为null
	 * @param allArguments	方法参数
	 * @param method	方法定义
	 * @return	需要传递到after的数据
	 * @throws Throwable	可以抛出异常，不影响正常调用
	 */
	BeforeResult<T> before(Object thisObj, Object[] allArguments, Method method) throws Throwable;

	/**
	 * 
	 * @param thisObj	拦截的方法类实例，如果拦截的是静态方法，则该值为null
	 * @param allArguments	方法参数
	 * @param method	方法定义
	 * @param ret	方法返回值
	 * @param t	方法抛出的异常，如果无异常则为null
	 * @param beforeResult	before的返回值
	 * @return 方法真正的返回值（如果不对原返回值处理默认直接返回ret即可）
	 */
	Object after(Object thisObj, Object[] allArguments, Method method, Object ret, Throwable t, BeforeResult<T> beforeResult);
}
