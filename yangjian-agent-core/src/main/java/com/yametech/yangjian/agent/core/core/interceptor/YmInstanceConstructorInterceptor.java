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
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

public class YmInstanceConstructorInterceptor {
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
//				log.warn(t, "interceptor constructor");// 业务异常，不打印
			}
		}

	}

}
