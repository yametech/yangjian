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

import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.util.RateLimit;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

public class YmInstanceConstructorInterceptor {
	private static final ILogger LOG = LoggerFactory.getLogger(YmInstanceConstructorInterceptor.class);
	private static final RateLimit LIMITER = RateLimit.create(10);
	private IConstructorListener[] interceptors;

	public YmInstanceConstructorInterceptor(IConstructorListener[] interceptors) {
		this.interceptors = interceptors;
	}

    @RuntimeType
    public void intercept(@This Object thisObj, @AllArguments Object[] allArguments) throws Throwable {
		for (IConstructorListener interceptor : interceptors) {
			try {
				interceptor.constructor(thisObj, allArguments);
			} catch (Throwable t) {
				if(LIMITER.tryAcquire()) {// 增加打印速率限制(每秒N条)，防止因插件写的有问题，发生大量异常时影响方法调用速度
					LOG.warn(t, "interceptor constructor exception");
				}
			}
		}

	}

}
