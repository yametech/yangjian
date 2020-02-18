/**
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

package com.yametech.yangjian.agent.core.aop;

import com.yametech.yangjian.agent.api.IMetricMatcher;
import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.IInterceptorInit;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.convert.IConvertMatcher;
import com.yametech.yangjian.agent.core.aop.base.MetricEventBus;
import com.yametech.yangjian.agent.core.core.classloader.InterceptorInstanceLoader;
import com.yametech.yangjian.agent.core.exception.AgentPackageNotFoundException;
import com.yametech.yangjian.agent.core.log.ILogger;
import com.yametech.yangjian.agent.core.log.LoggerFactory;

public class MetricMatcherProxy implements IInterceptorInit, InterceptorMatcher {
	private static ILogger log = LoggerFactory.getLogger(MetricMatcherProxy.class);
	private IMetricMatcher metricMatcher;
	private MetricEventBus metricEventBus;
	
	public MetricMatcherProxy(IMetricMatcher metricMatcher, MetricEventBus metricEventBus) {
		this.metricMatcher = metricMatcher;
		this.metricEventBus = metricEventBus;
	}
	
	@Override
	public void init(Object obj, ClassLoader classLoader, MethodType type) {
		if(!(obj instanceof BaseConvertAOP)) {
			return;
		}
		LoadClassKey convertClass = metricMatcher.loadClass(type);
		if(convertClass == null) {
			return;
		}
		try {
			Object convertInstance = InterceptorInstanceLoader.load(convertClass.getKey(), convertClass.getCls(), classLoader);
			if(convertInstance instanceof IConvertMatcher) {
				((IConvertMatcher)convertInstance).setMetricMatcher(metricMatcher);
			}
			((BaseConvertAOP) obj).init(convertInstance, metricEventBus, metricMatcher);
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException
				| AgentPackageNotFoundException e) {
			log.warn(e, "加载convert异常:{}", convertClass);
		}
	}

	@Override
	public IConfigMatch match() {
		return metricMatcher.match();
	}

	@Override
	public LoadClassKey loadClass(MethodType type) {
		LoadClassKey convertClass = metricMatcher.loadClass(type);
		if(convertClass == null) {
			return null;
		}
		// 其他type因无需求，未实现对应的类，如果后续有需求，可增加实现类
		String convertCls = null;
		if(MethodType.STATIC.equals(type)) {
			convertCls = "com.yametech.yangjian.agent.core.aop.base.ConvertStatisticMethodAOP";// TODO 此处使用getClass试试会不会有类加载问题，如果可行，就换成类加载，避免类换路径时此处不自动更换，也无法通过类依赖查询
		} else if(MethodType.INSTANCE.equals(type)) {
			convertCls = "com.yametech.yangjian.agent.core.aop.base.ConvertMethodAOP";
		}
		return convertCls == null ? null : new LoadClassKey(convertCls, "ConvertAOP:" + convertClass.getCls());
	}

}
