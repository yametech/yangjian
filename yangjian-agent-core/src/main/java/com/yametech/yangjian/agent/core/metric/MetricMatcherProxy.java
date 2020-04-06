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

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.IMetricMatcher;
import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.IMatcherProxy;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.convert.IConvertBase;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.core.InstanceManage;
import com.yametech.yangjian.agent.core.core.classloader.InterceptorInstanceLoader;
import com.yametech.yangjian.agent.core.metric.base.ConvertMethodAOP;
import com.yametech.yangjian.agent.core.metric.base.ConvertStatisticMethodAOP;
import com.yametech.yangjian.agent.core.metric.base.MetricEventBus;
import com.yametech.yangjian.agent.core.util.Util;
import com.yametech.yangjian.agent.core.util.Value;

public class MetricMatcherProxy implements IMatcherProxy<BaseConvertAOP, IMetricMatcher>, InterceptorMatcher {
	private static ILogger log = LoggerFactory.getLogger(MetricMatcherProxy.class);
	private static Map<MethodType, Class<?>> typeClass = new EnumMap<>(MethodType.class);
	private static Map<String, Object> convertAopInstance = new ConcurrentHashMap<>();
	private IMetricMatcher metricMatcher;
	private MetricEventBus metricEventBus;
	
	static {
		typeClass.put(MethodType.STATIC, ConvertStatisticMethodAOP.class);
		typeClass.put(MethodType.INSTANCE, ConvertMethodAOP.class);
	}
	
	public MetricMatcherProxy(IMetricMatcher metricMatcher) {
		this.metricMatcher = metricMatcher;
		this.metricEventBus = InstanceManage.getSpiInstance(MetricEventBus.class);
	}
	
	@Override
	public void init(BaseConvertAOP obj, ClassLoader classLoader, MethodType type) {
		LoadClassKey convertClass = metricMatcher.loadClass(type);
		if(convertClass == null) {
			return;
		}
		Object instance = null;
		try {
			instance = InterceptorInstanceLoader.load(convertClass.getKey(), convertClass.getCls(), classLoader);
			if(!(instance instanceof IConvertBase)) {
				throw new RuntimeException("metricMatcher配置的loadClass错误，必须为IConvertBase的子类");
			}
			if(instance instanceof IConfigReader) {
				InstanceManage.registryConfigReaderInstance((IConfigReader)instance);
			}
			obj.init(instance, metricEventBus, metricMatcher.type());
		} catch (Exception e) {
			log.warn(e, "加载convert异常：{}，\nclassLoader={}，\nmetricMatcher classLoader：{},\nconvertInstance classLoader：{}",
					convertClass, classLoader,
					Util.join(" > ", Util.listClassLoaders(metricMatcher.getClass())),
					Util.join(" > ", Util.listClassLoaders(instance == null ? null : instance.getClass())));
			throw new RuntimeException(e);
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
//		if(typeClass.containsKey(type)) {
//			convertCls = typeClass.get(type).getName();
//		}
		if(MethodType.STATIC.equals(type)) {
			convertCls =
					ConvertStatisticMethodAOP.class.getName();
//					"com.yametech.yangjian.agent.core.aop.base.ConvertStatisticMethodAOP";// TODO 此处使用getClass试试会不会有类加载问题，如果可行，就换成类加载，避免类换路径时此处不自动更换，也无法通过类依赖查询
		} else if(MethodType.INSTANCE.equals(type)) {
			convertCls =
					ConvertMethodAOP.class.getName();
//					"com.yametech.yangjian.agent.core.aop.base.ConvertMethodAOP";
		}
		return convertCls == null ? null : new LoadClassKey(convertCls, "ConvertAOP:" + convertClass.getCls());
	}
	
	/**
	 * 加载类
	 * @param className
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(String key, String className) throws Exception {
		Class<?> keyCls = null;
		for(Class<?> cls : typeClass.values()) {
			if(cls.getName().equals(className)) {
				keyCls = cls;
				break;
			}
		}
		if(keyCls == null) {
			return null;
		}
		Class<?> instanceCls = keyCls;
		Value<Exception> value = Value.absent();
		Object instance = convertAopInstance.computeIfAbsent(key, cls -> {
			try {
				return instanceCls.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				value.set(e);
				return null;
			}
		});
		if(value.get() != null) {
			throw value.get();
		}
		return (T) instance;
	}

}
