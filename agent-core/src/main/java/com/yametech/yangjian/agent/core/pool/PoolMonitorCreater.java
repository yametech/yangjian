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
package com.yametech.yangjian.agent.core.pool;

import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import com.yametech.yangjian.agent.api.interceptor.IDisableConfig;
import com.yametech.yangjian.agent.api.interceptor.IMethodAOP;
import com.yametech.yangjian.agent.api.interceptor.IStaticMethodAOP;
import com.yametech.yangjian.agent.api.pool.IPoolMonitor;
import com.yametech.yangjian.agent.api.pool.IPoolMonitorCreater;
import com.yametech.yangjian.agent.api.pool.IPoolMonitorMatcher;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 
 * @Description 
 * 
 * @author liuzhao
 * @date 2020年3月6日 下午3:14:14
 */
public class PoolMonitorCreater implements IMethodAOP<Long>, IConstructorListener, IStaticMethodAOP<Long>, IDisableConfig {
	protected IPoolMonitorCreater convert;
	private IPoolMonitorMatcher matcher;
	
	void init(IPoolMonitorMatcher matcher, IPoolMonitorCreater convert) {
		this.matcher = matcher;
		this.convert = convert;
	}

	@Override
	public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult<Long> beforeResult,
			Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
		register(thisObj, allArguments, method, beforeResult, ret, t, globalVar);
		return ret;
	}

	@Override
	public Object after(Object[] allArguments, Method method, BeforeResult<Long> beforeResult, Object ret, Throwable t,
			Map<Class<?>, Object> globalVar) throws Throwable {
		register(null, allArguments, method, beforeResult, ret, t, globalVar);
		return ret;
	}

	@Override
	public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
		register(thisObj, allArguments, null, null, null, null, null);
	}
	
	private void register(Object thisObj, Object[] allArguments, Method method, BeforeResult<Long> beforeResult,
			Object ret, Throwable t, Map<Class<?>, Object> globalVar) {
		if(convert == null) {
			return;
		}
		IPoolMonitor poolMonitor = convert.create(thisObj, allArguments, method, ret, t, globalVar);
		PoolMonitorRegistry.INSTANCE.register(poolMonitor);
	}
	
	@Override
	public BeforeResult<Long> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
		return null;
	}

	@Override
	public BeforeResult<Long> before(Object[] allArguments, Method method) throws Throwable {
		return null;
	}

	@Override
	public String disableKey() {
		return Constants.DISABLE_SPI_KEY_PREFIX + matcher.getClass().getSimpleName();
	}
}
