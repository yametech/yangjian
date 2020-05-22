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

import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.common.InstanceManage;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.api.pool.IPoolMonitorCreater;
import com.yametech.yangjian.agent.api.pool.IPoolMonitorMatcher;
import com.yametech.yangjian.agent.core.common.BaseMatcherProxy;
import com.yametech.yangjian.agent.core.core.classloader.InterceptorInstanceLoader;
import com.yametech.yangjian.agent.core.util.Util;

/**
 * 
 * @Description PoolMonitorMatcher实例代理器
 * 
 * @author liuzhao
 * @date 2020年3月6日 下午3:14:22
 */
public class PoolMonitorMatcherProxy extends BaseMatcherProxy<IPoolMonitorMatcher, PoolMonitorCreater> {
	private static ILogger log = LoggerFactory.getLogger(PoolMonitorMatcherProxy.class);
	
	public PoolMonitorMatcherProxy(IPoolMonitorMatcher matcher) {
		super(matcher);
	}
	
	@Override
	public void init(PoolMonitorCreater obj, ClassLoader classLoader, MethodType type, MethodDefined methodDefined) {
		LoadClassKey loadClassKey = matcher.loadClass(type, methodDefined);
		if(loadClassKey == null) {
			return;
		}
		Object instance = null;
		try {
			instance = InterceptorInstanceLoader.load(loadClassKey.getKey(), loadClassKey.getCls(), classLoader);
			if(!(instance instanceof IPoolMonitorCreater)) {
				throw new RuntimeException("poolMonitorMatcher配置的loadClass错误，必须为IPoolMonitorCreater的子类");
			}
			InstanceManage.registryInit(instance);
			obj.init(matcher, (IPoolMonitorCreater)instance);
		} catch (Exception e) {
			log.warn(e, "加载异常：{}，\nclassLoader={}，\nmetricMatcher classLoader：{},\ninstance classLoader：{}",
					loadClassKey, classLoader,
					Util.join(" > ", Util.listClassLoaders(matcher.getClass())),
					Util.join(" > ", Util.listClassLoaders(instance == null ? null : instance.getClass())));
			throw new RuntimeException(e);
		}
	}

	@Override
	public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
		LoadClassKey loadClassKey = matcher.loadClass(type, methodDefined);
		if(loadClassKey == null) {
			return null;
		}
		return new LoadClassKey(PoolMonitorCreater.class.getName(), "PoolMonitor:" + loadClassKey.getCls());
	}
	
}
