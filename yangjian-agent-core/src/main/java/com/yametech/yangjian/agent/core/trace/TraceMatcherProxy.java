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
package com.yametech.yangjian.agent.core.trace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.api.trace.ICustomLoad;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanCustom;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.ITraceMatcher;
import com.yametech.yangjian.agent.api.trace.SampleStrategy;
import com.yametech.yangjian.agent.core.common.BaseMatcherProxy;
import com.yametech.yangjian.agent.core.core.InstanceManage;
import com.yametech.yangjian.agent.core.core.classloader.InterceptorInstanceLoader;
import com.yametech.yangjian.agent.core.exception.AgentPackageNotFoundException;
import com.yametech.yangjian.agent.core.trace.base.ITraceDepend;
import com.yametech.yangjian.agent.core.trace.sample.FollowerRateLimitSampler;
import com.yametech.yangjian.agent.core.trace.sample.FollowerSampler;
import com.yametech.yangjian.agent.core.trace.sample.RateLimitSampler;
import com.yametech.yangjian.agent.core.trace.sample.SampleFactory;
import com.yametech.yangjian.agent.core.util.Util;

import brave.Tracing;

public class TraceMatcherProxy extends BaseMatcherProxy<ITraceMatcher, TraceAOP<?>> implements IConfigReader {
	private static ILogger log = LoggerFactory.getLogger(TraceMatcherProxy.class);
//	private static AsyncReporter<Span> report = AsyncReporter.builder(OkHttpSender.newBuilder().endpoint("http://localhost:9411/api/v2/spans").compressionEnabled(false).build()).build();
	private static final String KEY_QPS_GLOBAL = "trace.sample.qps.global";
	private static final String KEY_QPS_DEFAULT = "trace.sample.qps.default";
	private static final String KEY_PREFIX_TYPE_STRATEGY = "trace.sample.strategy.";
	private static final String KEY_PREFIX_TYPE_QPS = "trace.sample.qps.";
	
	private static ISpanSample globalSample;
	private ISpanSample spanSample;
//	private TraceEventBus traceCache;
	
	public TraceMatcherProxy(ITraceMatcher matcher) {
		super(matcher);
//		this.traceCache = InstanceManage.getSpiInstance(TraceEventBus.class);
	}
	
	@Override
	public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
		return new LoadClassKey(TraceAOP.class.getName(), "TraceAOP:" + matcher.loadClass(type, methodDefined).getCls());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void init(TraceAOP aop, ClassLoader classLoader, MethodType type, MethodDefined methodDefined) {
		LoadClassKey loadClassKey = matcher.loadClass(type, methodDefined);
		if(loadClassKey == null) {
			return;
		}
		Object instance = null;
		try {
			instance = InterceptorInstanceLoader.load(loadClassKey.getKey(), loadClassKey.getCls(), classLoader);
			if(!(instance instanceof ISpanCreater)) {
				throw new RuntimeException("loadClass配置错误，必须为ISpanCreater的子类");
			}
			InstanceManage.registryInit(instance);
			if(instance instanceof ICustomLoad) {// 指定了定制tag接口，加载实现类
				ICustomLoad customLoad = (ICustomLoad)instance;
				List<ISpanCustom> spanCustoms = getCustomInstance(customLoad, classLoader);
				if(spanCustoms != null) {
					customLoad.custom(spanCustoms);
				}
			}

			// 测试上报到zipkin后端
//			Tracing tracing = BraveHelper.getTracing(report, null);
//			Tracing tracing = BraveHelper.getTracing(span -> traceCache.publish(t -> t.setSpan(span)), null);
			Tracing tracing = InstanceManage.getInstance(Tracing.class);
			if(spanSample instanceof ITraceDepend) {
				((ITraceDepend)spanSample).tracer(tracing.tracer());
			}
			aop.init((ISpanCreater)instance, tracing, spanSample);
		} catch (Exception e) {
			log.warn(e, "加载异常：{}，\nclassLoader={}，\nmatcher classLoader：{},\ninstance classLoader：{}",
					loadClassKey, classLoader,
					Util.join(" > ", Util.listClassLoaders(matcher.getClass())),
					Util.join(" > ", Util.listClassLoaders(instance == null ? null : instance.getClass())));
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private List<ISpanCustom> getCustomInstance(ICustomLoad customLoad, ClassLoader classLoader) throws IllegalAccessException, InstantiationException, ClassNotFoundException, AgentPackageNotFoundException {
		Class<?> cls = null;
		try {
			cls = Util.superClassGeneric(customLoad.getClass(), 0);
		} catch (Exception e) {
			log.warn(e, "无法识别泛型：{}，不设置customLoad", customLoad.getClass());
			return null;
		}
		List<String> customClassNames = InstanceManage.getSPIClass(cls);
		if(customClassNames == null || customClassNames.isEmpty()) {
			return null;
		}
		List<ISpanCustom> customs = new ArrayList<>();
		for(String loadClassName : customClassNames) {
			customs.add(InterceptorInstanceLoader.load(loadClassName, loadClassName, classLoader));
		}
		return customs;
	}
	
	@Override
	public Set<String> configKey() {
		return new HashSet<>(Arrays.asList(KEY_QPS_GLOBAL.replaceAll("\\.", "\\\\."), KEY_QPS_DEFAULT.replaceAll("\\.", "\\\\."), 
				KEY_PREFIX_TYPE_STRATEGY.replaceAll("\\.", "\\\\.") + matcher.type().getKey(), 
				KEY_PREFIX_TYPE_QPS.replaceAll("\\.", "\\\\.") + matcher.type().getKey()));
	}

	/**
	 * 这里无并发
	 */
	@Override
	public void configKeyValue(Map<String, String> kv) {
		if(kv == null) {
			return;
		}
		SampleStrategy sampleStrategy = SampleStrategy.FOLLOWER;
		String sampleStrategyConfig = kv.get(KEY_PREFIX_TYPE_STRATEGY + matcher.type().getKey());
		if(sampleStrategyConfig != null) {
			sampleStrategy = SampleStrategy.getOrDefault(sampleStrategyConfig);
		}
		if(SampleStrategy.NONE.equals(sampleStrategy)) {
			spanSample = SampleFactory.NONE;
			return;
		}
		if(SampleStrategy.ALWAYS.equals(sampleStrategy)) {
			spanSample = SampleFactory.ALWAYS;
			return;
		}
		
		if(SampleStrategy.FOLLOWER.equals(sampleStrategy)) {
			spanSample = new FollowerSampler();
			return;
		}
		
		String globalQPSConfig = kv.get(KEY_QPS_GLOBAL);
		if(globalQPSConfig != null) {
			int globalQPS = Integer.parseInt(globalQPSConfig);
			synchronized (TraceMatcherProxy.class) {
				if(globalSample == null) {
					globalSample = new RateLimitSampler(globalQPS);
				}
			}
			spanSample = globalSample;
			return;
		}
		
		int sampleQPS = 10;
		String sampleQPSConfig = kv.get(KEY_PREFIX_TYPE_QPS + matcher.type().getKey());
		if(sampleQPSConfig != null) {
			sampleQPS = Integer.parseInt(sampleQPSConfig);
		} else {
			String defaultQPSConfig = kv.get(KEY_QPS_DEFAULT);
			if(defaultQPSConfig != null) {
				sampleQPS = Integer.parseInt(defaultQPSConfig);
			}
		}
		if(SampleStrategy.LEADER.equals(sampleStrategy)) {
			spanSample = new RateLimitSampler(sampleQPS);
		} else if(SampleStrategy.FOLLOWERANDLEADER.equals(sampleStrategy)) {
			spanSample = new FollowerRateLimitSampler(sampleQPS);
		}
	}
}
