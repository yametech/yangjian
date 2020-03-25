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
package com.yametech.yangjian.agent.core.core.interceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.interceptor.IStaticMethodAOP;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class YmStaticInterceptor {
	private static final ILogger log = LoggerFactory.getLogger(YmStaticInterceptor.class);
//	private static final MethodType TYPE = MethodType.STATIC;
    private InterceptorWrapper<IStaticMethodAOP<?>>[] interceptors;
    
    public YmStaticInterceptor(InterceptorWrapper<IStaticMethodAOP<?>>[] interceptors) {
    	this.interceptors = interceptors;
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RuntimeType
	public Object intercept(@Origin Class<?> clazz, @AllArguments Object[] allArguments, @SuperCall Callable<?> callable, @Origin Method method)
			throws Throwable {
//      if(interceptors == null || interceptors.length == 0) {// 无需判断，不会为null或者length为0，减少性能损耗
//      return callable.call();
//  }
		InterceptBean<IStaticMethodAOP<?>>[] interceptBeans = new InterceptBean[interceptors.length];
		int index = 0;
		Object ret = null;
		Map<Class<?>, Object> globalVar = null;
		for (InterceptorWrapper<IStaticMethodAOP<?>> interceptorWrapper : interceptors) {
		    if (!interceptorWrapper.isEnable()) {
		    	continue;
			}
			IStaticMethodAOP<?> interceptor = interceptorWrapper.getInterceptor();
			try {
				BeforeResult<?> result = interceptor.before(allArguments, method);
				interceptBeans[index++] = new InterceptBean<>(interceptor, result);
				if (result != null) {
					if (result.getRet() != null) {
						ret = result.getRet();
					}
					if (result.getGlobalVar() != null) {
						if (globalVar == null) {
							globalVar = new HashMap<>();
						}
						globalVar.put(interceptor.getClass(), result.getGlobalVar());
					}
				}
			} catch (Throwable t) {
				log.warn(t, "interceptor before");// TODO 增加打印速率限制(每秒N条)，防止因插件写的有问题，发生大量异常时影响方法调用速度
				// before异常，不再执行after和exception，通过index控制
			}
		}

		Throwable methodThrowable = null;
		if (ret == null && callable != null) {
			try {
				ret = callable.call();
			} catch (Throwable t) {
//				log.warn(t, "interceptor call");// 业务异常，不打印
				methodThrowable = t;
			}
		}

		for (int i = index - 1; i >= 0; i--) {
			IStaticMethodAOP<?> interceptor = interceptBeans[i].getInterceptor();
			BeforeResult result = interceptBeans[i].getResult();
			try {
				if (methodThrowable != null) {
					// exception处理异常，不再执行after
					interceptor.exception(allArguments, method, result, methodThrowable, globalVar);
				}
				ret = interceptor.after(allArguments, method, result, ret, methodThrowable, globalVar);
			} catch (Throwable t) {
				log.warn(t, "interceptor exception/after");// TODO 增加打印速率限制(每秒N条)，防止因插件写的有问题，发生大量异常时影响方法调用速度
			}
		}
		if (methodThrowable == null) {
			return ret;
		} else {
			throw methodThrowable;
		}
	}

}
