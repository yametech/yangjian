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

import brave.Tracing;
import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.common.InstanceManage;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.api.trace.*;
import com.yametech.yangjian.agent.core.common.BaseMatcherProxy;
import com.yametech.yangjian.agent.core.core.classloader.InterceptorInstanceLoader;
import com.yametech.yangjian.agent.core.core.classloader.SpiLoader;
import com.yametech.yangjian.agent.core.trace.base.BraveHelper;
import com.yametech.yangjian.agent.core.trace.base.ITraceDepend;
import com.yametech.yangjian.agent.core.trace.base.TraceEventBus;
import com.yametech.yangjian.agent.core.trace.sample.FollowerRateLimitSampler;
import com.yametech.yangjian.agent.core.trace.sample.FollowerSampler;
import com.yametech.yangjian.agent.core.trace.sample.RateLimitSampler;
import com.yametech.yangjian.agent.core.trace.sample.SampleFactory;
import com.yametech.yangjian.agent.core.util.Util;

import java.util.*;

public class TraceMatcherProxy extends BaseMatcherProxy<ITraceMatcher, TraceAOP<?>> implements IConfigReader {
	private static ILogger log = LoggerFactory.getLogger(TraceMatcherProxy.class);
//	private static AsyncReporter<Span> report = AsyncReporter.builder(OkHttpSender.newBuilder().endpoint("http://localhost:9411/api/v2/spans").compressionEnabled(false).build()).build();
	private static final String KEY_QPS_GLOBAL = "trace.sample.qps.global";
	private static final String KEY_QPS_DEFAULT = "trace.sample.qps.default";
	private static final String KEY_PREFIX_TYPE_STRATEGY = "trace.sample.strategy.";
	private static final String KEY_PREFIX_TYPE_QPS = "trace.sample.qps.";
	
	private static ISpanSample globalSample;
	private static Tracing tracing;
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
			Tracing tracing = getTracing();// 单例
			if(tracing == null) {
				throw new RuntimeException("未初始化Tracing实例");
			}
			if(spanSample instanceof ITraceDepend) {
				((ITraceDepend)spanSample).tracer(tracing.tracer());
			}
			aop.init(this.matcher, (ISpanCreater)instance, tracing, spanSample);
		} catch (Throwable e) {
			log.warn(e, "load TraceAOP exception: {}，\n\t\tclassLoader={}，\n\t\tmatcher classLoader：{},\n\t\tinstance classLoader：{}",
					loadClassKey, classLoader,
					Util.join(" > ", Util.listClassLoaders(matcher.getClass())),
					Util.join(" > ", Util.listClassLoaders(instance == null ? null : instance.getClass())));
			throw new RuntimeException(e);
		}
	}

	/**
	 * 仅初始化时调用，所以此处没有加两次检查提高性能
	 * @return
	 */
	private synchronized static Tracing getTracing() {
		if(tracing != null) {
			return tracing;
		}
		TraceEventBus publish = new TraceEventBus();
		InstanceManage.registryInit(publish);
		tracing = BraveHelper.getTracing(span -> publish.publish(t -> t.setSpan(span)), null);
		return tracing;
	}
	
	@SuppressWarnings("rawtypes")
	private List<ISpanCustom> getCustomInstance(ICustomLoad customLoad, ClassLoader classLoader) throws Throwable {
		Class<?> cls;
		try {
			cls = Util.superClassGeneric(customLoad.getClass(), 0);
		} catch (Exception e) {
			log.warn(e, "无法识别泛型：{}，不设置customLoad", customLoad.getClass());
			return null;
		}
		List<String> customClassNames = SpiLoader.getSpiClass(cls);
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
