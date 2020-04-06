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
package com.yametech.yangjian.agent.core;

import static net.bytebuddy.matcher.ElementMatchers.isInterface;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.api.IConfigLoader;
import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.IEnhanceClassMatch;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.IMatcherProxy;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.configmatch.CombineOrMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodRegexMatch;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.config.Config;
import com.yametech.yangjian.agent.core.core.InstanceManage;
import com.yametech.yangjian.agent.core.core.agent.AgentListener;
import com.yametech.yangjian.agent.core.core.agent.AgentTransformer;
import com.yametech.yangjian.agent.core.core.classloader.AgentClassLoader;
import com.yametech.yangjian.agent.core.core.elementmatch.ClassElementMatcher;
import com.yametech.yangjian.agent.core.metric.MetricData;
import com.yametech.yangjian.agent.core.metric.MetricMatcherProxy;
import com.yametech.yangjian.agent.core.pool.PoolMonitorMatcherProxy;
import com.yametech.yangjian.agent.core.report.ReportManage;
import com.yametech.yangjian.agent.core.trace.TraceMatcherProxy;
import com.yametech.yangjian.agent.core.util.Util;
import com.yametech.yangjian.agent.util.CustomThreadFactory;
import com.yametech.yangjian.agent.util.OSUtil;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatchers;

public class YMAgent {
	private static ILogger log = LoggerFactory.getLogger(YMAgent.class);
	private static IReportData report = ReportManage.getReport("YMAgent");
	private static ScheduledExecutorService service;
	private static final String[] IGNORE_CLASS_CONFIG = new String[] {"^net\\.bytebuddy\\.", "^org\\.slf4j\\.", // ".*\\$auxiliary\\$.*", 
			"^org\\.apache\\.logging\\.", "^org\\.groovy\\.", "^sun\\.reflect\\.", // ".*javassist.*", ".*\\.asm\\..*", 这两个会有误拦截：com.alibaba.dubbo.rpc.proxy.javassist.JavassistProxyFactory
			"^org\\.apache\\.skywalking\\.", "^com\\.yametech\\.yangjian\\.agent\\."};
	private static final String[] IGNORE_METHOD_CONFIG = new String[] {".*toString\\(\\)$", ".*equals\\(java.lang.Object\\)$",
            ".*hashCode\\(\\)$", ".*clone\\(\\).*"};
	private static final Map<Class<?>, Class<?>> MATCHER_PROXY_CLASS;
	
	static {
		MATCHER_PROXY_CLASS = new HashMap<>();
		for(Class<?> matcher : Arrays.asList(MetricMatcherProxy.class, PoolMonitorMatcherProxy.class, TraceMatcherProxy.class)) {// 此处手动维护，后续自动发现
			MATCHER_PROXY_CLASS.put(Util.interfacesGeneric(matcher, IMatcherProxy.class, 1), matcher);
		}
	}
	
	/**
	 * -javaagent:E:\eclipse-workspace\tool-ecpark-monitor\ecpark-agent\dist\ecpark-agent\ecpark-agent.jar=args -Dskywalking.agent.service_name=testlog
	 * 
	 * @param arguments		javaagent=号后的文本，例如：-javaagent:E:\eclipse-workspace\tool-ecpark-monitor\ecpark-agent\target\ecpark-agent.jar=arg=123，此时arguments=arg=123
	 * @param instrumentation
	 * @throws Exception
	 */
    public static void premain(String arguments, Instrumentation instrumentation) throws Exception{
    	log.info("os: {}, {}", OSUtil.OS, arguments);
    	if(StringUtil.isEmpty(Config.SERVICE_NAME.getValue())) {
    		log.warn("未配置应用名称，跳过代理");
    		return;
    	}
    	System.setProperty(Constants.SYSTEM_PROPERTIES_PREFIX + Constants.SERVICE_NAME, Config.SERVICE_NAME.getValue());
    	AgentClassLoader.initDefaultLoader();
    	InstanceManage.loadSpi();
    	InstanceManage.getSpis().forEach(spi -> log.debug("spiClassLoader:{}, {}", spi, Util.join(" > ", Util.listClassLoaders(spi.getClass()))));
    	loadConfig(arguments);
    	InstanceManage.notifyReader();
    	beforeRun();
    	startSchedule();
    	addShutdownHook();
    	instrumentation(instrumentation);
    	// 埋点日志，不允许删除
    	report.report(MetricData.get("status/" + Constants.Status.STARTING));
    }
    
    private static void instrumentation(Instrumentation instrumentation) {
    	List<InterceptorMatcher> interceptorMatchers = InstanceManage.listSpiInstance(InterceptorMatcher.class).stream().collect(Collectors.toList());
    	List<IConfigMatch> matches = interceptorMatchers.stream().filter(aop -> aop.match() != null)
    			.map(InterceptorMatcher::match).collect(Collectors.toList());
    	interceptorMatchers = interceptorMatchers.stream().map(YMAgent::getMatcherProxy).collect(Collectors.toList());// 转换IMetricMatcher为MetricMatcherProxy
    	List<IEnhanceClassMatch> classMatches = InstanceManage.listSpiInstance(IEnhanceClassMatch.class);
    	matches.addAll(classMatches.stream().filter(aop -> aop.classMatch() != null).map(IEnhanceClassMatch::classMatch).collect(Collectors.toList()));
    	log.info("match class:{}", matches);
    	IConfigMatch ignoreMatch = getOrRegexMatch(Config.IGNORE_CLASS.getValue(), IGNORE_CLASS_CONFIG);
    	log.info("ignore class:{}", ignoreMatch);
    	IConfigMatch ignoreMethodMatch = getOrRegexMatch(Config.IGNORE_METHODS.getValue(), IGNORE_METHOD_CONFIG);
    	log.info("ignore method:{}", ignoreMethodMatch);
        new AgentBuilder.Default()
                .ignore(isInterface()
                        .or(ElementMatchers.<TypeDescription>isSynthetic())
                		.or(new ClassElementMatcher(ignoreMatch, "class_ignore")))// byte-buddy代理的类会包含该字符串
//        		.with(AgentBuilder.LambdaInstrumentationStrategy.ENABLED)
                .type(new ClassElementMatcher(new CombineOrMatch(matches), "class_match"))
//                .type(ElementMatchers.nameEndsWith("Timed"))
                .transform(new AgentTransformer(interceptorMatchers, ignoreMethodMatch, classMatches))
//                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)// 使用后会异常
                .with(new AgentListener())
                .installOn(instrumentation);
    }
    
    private static InterceptorMatcher getMatcherProxy(InterceptorMatcher matcher) {
    	Class<?> proxy = MATCHER_PROXY_CLASS.get(matcher.getClass());
    	if(proxy == null) {
    		return matcher;
    	}
    	try {
    		InterceptorMatcher proxyMatcher = (InterceptorMatcher) proxy.getConstructor(matcher.getClass()).newInstance(matcher);
    		if(proxyMatcher instanceof IConfigReader) {
    			InstanceManage.registryConfigReaderInstance((IConfigReader)proxyMatcher);
    		}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.warn(e, "转换proxy异常：{}", matcher.getClass());
		}
    	return matcher;
    }

    private static IConfigMatch getOrRegexMatch(Set<String> configs, String... extra) {
    	List<IConfigMatch> matches = new ArrayList<>();
    	if(extra != null) {
    		for(String s : extra) {
    			matches.add(new MethodRegexMatch(s));
    		}
    	}
    	if(configs != null) {
    		for(String s : configs) {
    			matches.add(new MethodRegexMatch(s));
    		}
    	}
    	return new CombineOrMatch(matches);
    }
    
    /**
     * 初始化配置
     * @throws Exception 
     */
    private static void loadConfig(String arguments) throws Exception {
    	for(IConfigLoader loader: InstanceManage.listSpiInstance(IConfigLoader.class)) {
    		loader.load(arguments);
    	}
    }
    
    /**
     * 初始化逻辑
     */
    private static void beforeRun() {
    	InstanceManage.listSpiInstance(IAppStatusListener.class).forEach(IAppStatusListener::beforeRun);
    }

    /**
     * 开启定时调度
     */
    private static void startSchedule() {
    	service = Executors.newScheduledThreadPool(Config.SCHEDULE_CORE_POOL_SIZE.getValue(), new CustomThreadFactory("schedule", true));
    	InstanceManage.listSpiInstance(ISchedule.class).forEach(schedule -> {
    		if(schedule.initialDelay() == 0) {
    			schedule.execute();
    		}
    	});// 执行一次定时任务，防止多线程类加载死锁
    	InstanceManage.listSpiInstance(ISchedule.class).forEach(schedule -> {
    		int delay = schedule.initialDelay();
    		if(delay == 0) {
    			delay += schedule.interval();
    		}
    		service.scheduleAtFixedRate(() -> {
    			try {
    				schedule.execute();
    			} catch(Exception e) {
    				log.warn(e, "执行定时任务异常：{}", schedule.getClass());
    			}
    		} , delay, schedule.interval(), schedule.timeUnit());
    	});
	}
    
    /**
     * 注册关闭通知
     */
    private static void addShutdownHook() {
    	Runtime.getRuntime().addShutdownHook(new Thread() {// 注意关闭应用不能使用kill -9，会导致下面的方法不执行
            @Override
    		public void run() {
            	// 埋点日志，不允许删除
            	report.report(MetricData.get("status/" + Constants.Status.CLOSING));
            	List<IAppStatusListener> shutdowns = InstanceManage.listSpiInstance(IAppStatusListener.class);
            	Collections.reverse(shutdowns);// 启动时顺序init，关闭时倒序showdown
            	try {
	            	for(IAppStatusListener spi : shutdowns) {
	        			spi.shutdown(Duration.ofSeconds(10));
	            	}
	            	service.shutdown();
					service.awaitTermination(5, TimeUnit.SECONDS);
				} catch (Exception e) {
					log.warn(e, "关闭服务异常，可能丢失数据");
				}
            	// 埋点日志，不允许删除
            	report.report(MetricData.get("status/" + Constants.Status.CLOSED));
            }
        });
    }
    
}
