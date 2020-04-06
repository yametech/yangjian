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
package com.yametech.yangjian.agent.api.base;

import com.yametech.yangjian.agent.api.InterceptorMatcher;

/**
 * 将实例对象转换为interception对象
 * 
 * @author liuzhao
 * 2019年12月21日 下午10:21:04
 */
public interface IMatcherProxy<T, V extends InterceptorMatcher> {
	
	/**
	 * 初始化Interceptor
	 * @param obj	返回值必须实现IConstructorListener、IMethodAOP、IStaticMethodAOP中的一个
	 * @param classLoader	类加载器
	 * @param type 方法类型
	 */
	void init(T obj, ClassLoader classLoader, MethodType type);
	
//	default Class<V> interceptClass() {
//		Type[] types = this.getClass().getGenericInterfaces();
//		ParameterizedType parameterized = (ParameterizedType) types[0];
//		if(parameterized.getActualTypeArguments() != null && parameterized.getActualTypeArguments().length > 1) {
//			return (Class<V>) parameterized.getActualTypeArguments()[1];
//		}
//		return null;
//	}
}
