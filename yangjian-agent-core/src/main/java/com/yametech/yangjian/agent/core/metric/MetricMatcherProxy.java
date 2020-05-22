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
package com.yametech.yangjian.agent.core.metric;

import com.yametech.yangjian.agent.api.IMetricMatcher;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.common.InstanceManage;
import com.yametech.yangjian.agent.api.convert.IConvertBase;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.common.BaseMatcherProxy;
import com.yametech.yangjian.agent.core.core.classloader.InterceptorInstanceLoader;
import com.yametech.yangjian.agent.core.metric.base.ConvertMethodAOP;
import com.yametech.yangjian.agent.core.metric.base.ConvertStatisticMethodAOP;
import com.yametech.yangjian.agent.core.metric.base.MetricEventBus;
import com.yametech.yangjian.agent.core.util.Util;

public class MetricMatcherProxy extends BaseMatcherProxy<IMetricMatcher, BaseConvertAOP> {
	private static ILogger log = LoggerFactory.getLogger(MetricMatcherProxy.class);
	private MetricEventBus metricEventBus;
	
	public MetricMatcherProxy(IMetricMatcher metricMatcher) {
		super(metricMatcher);
		this.metricEventBus = InstanceManage.getInstance(MetricEventBus.class);
	}
	
	@Override
	public void init(BaseConvertAOP obj, ClassLoader classLoader, MethodType type, MethodDefined methodDefined) {
		LoadClassKey convertClass = matcher.loadClass(type, methodDefined);
		if(convertClass == null) {
			return;
		}
		Object instance = null;
		try {
			instance = InterceptorInstanceLoader.load(convertClass.getKey(), convertClass.getCls(), classLoader);
			if(!(instance instanceof IConvertBase)) {
				throw new RuntimeException("metricMatcher配置的loadClass错误，必须为IConvertBase的子类");
			}
			InstanceManage.registryInit(instance);
			obj.init(matcher, instance, metricEventBus, matcher.type());
		} catch (Exception e) {
			log.warn(e, "加载convert异常：{}，\nclassLoader={}，\nmetricMatcher classLoader：{},\nconvertInstance classLoader：{}",
					convertClass, classLoader,
					Util.join(" > ", Util.listClassLoaders(matcher.getClass())),
					Util.join(" > ", Util.listClassLoaders(instance == null ? null : instance.getClass())));
			throw new RuntimeException(e);
		}
	}

	@Override
	public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
		LoadClassKey convertClass = matcher.loadClass(type, methodDefined);
		if(convertClass == null) {
			return null;
		}
		// 其他type因无需求，未实现对应的类，如果后续有需求，可增加实现类
		String convertCls = null;
//		if(typeClass.containsKey(type)) {
//			convertCls = typeClass.get(type).getName();
//		}
		if(MethodType.STATIC.equals(type)) {
			convertCls = ConvertStatisticMethodAOP.class.getName();
		} else if(MethodType.INSTANCE.equals(type)) {
			convertCls = ConvertMethodAOP.class.getName();
		}
		return convertCls == null ? null : new LoadClassKey(convertCls, "ConvertAOP:" + convertClass.getCls());
	}
	
	

}
