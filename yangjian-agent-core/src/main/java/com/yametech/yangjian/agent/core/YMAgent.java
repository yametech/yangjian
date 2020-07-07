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

import com.yametech.yangjian.agent.api.IEnhanceClassMatch;
import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.common.Config;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.InstanceManage;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.configmatch.CombineOrMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodRegexMatch;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.common.CoreConstants;
import com.yametech.yangjian.agent.core.common.MatchProxyManage;
import com.yametech.yangjian.agent.core.core.agent.AgentListener;
import com.yametech.yangjian.agent.core.core.agent.AgentTransformer;
import com.yametech.yangjian.agent.core.core.classloader.SpiLoader;
import com.yametech.yangjian.agent.core.core.elementmatch.ClassElementMatcher;
import com.yametech.yangjian.agent.core.util.Util;
import com.yametech.yangjian.agent.util.OSUtil;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import static net.bytebuddy.matcher.ElementMatchers.isInterface;

public class YMAgent {
	private static final ILogger LOG = LoggerFactory.getLogger(YMAgent.class);
	private static final String[] IGNORE_CLASS_CONFIG = new String[] {"^net\\.bytebuddy\\.", "^org\\.slf4j\\.", // ".*\\$auxiliary\\$.*", 
			"^org\\.apache\\.logging\\.", "^org\\.groovy\\.", "^sun\\.reflect\\.", // ".*javassist.*", ".*\\.asm\\..*", 这两个会有误拦截：com.alibaba.dubbo.rpc.proxy.javassist.JavassistProxyFactory
			"^org\\.apache\\.skywalking\\.", "^javassist\\.", "^java\\.util\\."};//, "^com\\.yametech\\.yangjian\\.agent\\."};
	private static final String[] IGNORE_METHOD_CONFIG = new String[] {".*toString\\(\\)$", ".*equals\\(java.lang.Object\\)$",
            ".*hashCode\\(\\)$", ".*clone\\(\\).*"};

	private static final List<InterceptorMatcher> TRANSFORMER_MATCHERS = new CopyOnWriteArrayList<>();
	private static final List<IConfigMatch> TYPE_MATCHES = new CopyOnWriteArrayList<>();
	private static final List<IConfigMatch> IGNORE_MATCHES = new CopyOnWriteArrayList<>();
	private static final List<IConfigMatch> IGNORE_METHOD_MATCHES = new CopyOnWriteArrayList<>();
	private static final List<IEnhanceClassMatch> CLASS_MATCHES = new CopyOnWriteArrayList<>();
	private static final Set<String> IGNORE_CLASS_LOADER_NAME = new CopyOnWriteArraySet<>();

	/**
	 * -javaagent:E:\eclipse-workspace\tool-ecpark-monitor\ecpark-agent\dist\ecpark-agent\ecpark-agent.jar=args -Dskywalking.agent.service_name=testlog
	 * 
	 * @param arguments		javaagent=号后的文本，例如：-javaagent:E:\eclipse-workspace\tool-ecpark-monitor\ecpark-agent\target\ecpark-agent.jar=arg=123，此时arguments=arg=123
	 * @param instrumentation	类增强
	 */
    public static void premain(String arguments, Instrumentation instrumentation) {
    	LOG.info("os: {}, {}, {}", OSUtil.OS, arguments, YMAgent.class.getClassLoader().getClass());
    	if(StringUtil.isEmpty(Config.SERVICE_NAME.getValue())) {
    		LOG.warn("Missing service name config, skip agent.");
    		return;
    	}
    	System.setProperty(Constants.SYSTEM_PROPERTIES_PREFIX + Constants.SERVICE_NAME, Config.SERVICE_NAME.getValue());
		instrumentation(instrumentation);// 类增强放到最开始，防止部分增强类提前加载了，导致无法增强
		SpiLoader.loadSpi();
		if(LOG.isDebugEnable()) {
			InstanceManage.listSpiClass().forEach(spi -> LOG.debug("spiClassLoader:{}, {}", spi, Util.join(" > ", Util.listClassLoaders(spi))));
		}
		InstanceManage.loadConfig(arguments);
		// 如果禁用了所有插件则直接返回不执行下面拦截逻辑
		if (CoreConstants.CONFIG_KEY_DISABLE.equals(Config.getKv(CoreConstants.SPI_PLUGIN_KEY))) {
			LOG.warn("Disable all plugins, skip agent.");
			return;
		}
		InstanceManage.notifyReader();
		InstanceManage.beforeRun();
		InstanceManage.startSchedule();
		addShutdownHook();
		initMatch();// 更新增强类过滤条件
    }

    private static void instrumentation(Instrumentation instrumentation) {
		for(String s : IGNORE_CLASS_CONFIG) {
			IGNORE_MATCHES.add(new MethodRegexMatch(s));
		}
		for(String s : IGNORE_METHOD_CONFIG) {
			IGNORE_METHOD_MATCHES.add(new MethodRegexMatch(s));
		}
    	new AgentBuilder.Default()
				.ignore(isInterface()
						.or(ElementMatchers.<TypeDescription>isSynthetic())
						.or(new ClassElementMatcher(new CombineOrMatch(IGNORE_MATCHES), "class_ignore")))// byte-buddy代理的类会包含该字符串
//        		.with(AgentBuilder.LambdaInstrumentationStrategy.ENABLED)
				.type(new ClassElementMatcher(new CombineOrMatch(TYPE_MATCHES), "class_match"))
//                .type(ElementMatchers.nameEndsWith("Timed"))
				.transform(new AgentTransformer(TRANSFORMER_MATCHERS, new CombineOrMatch(IGNORE_METHOD_MATCHES), CLASS_MATCHES, IGNORE_CLASS_LOADER_NAME))
//                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)// 使用后会异常
				.with(new AgentListener())
				.installOn(instrumentation);
	}

    private static void initMatch() {
		IGNORE_CLASS_LOADER_NAME.addAll(Config.IGNORE_CLASSLOADERNAMES.getValue());
    	List<InterceptorMatcher> interceptorMatchers = new ArrayList<>(InstanceManage.listInstance(InterceptorMatcher.class));
    	interceptorMatchers.stream().filter(aop -> aop.match() != null).map(InterceptorMatcher::match).forEachOrdered(TYPE_MATCHES::add);
    	interceptorMatchers.stream().map(YMAgent::getMatcherProxy).forEachOrdered(TRANSFORMER_MATCHERS::add);// 转换IMetricMatcher为MetricMatcherProxy
		CLASS_MATCHES.addAll(InstanceManage.listInstance(IEnhanceClassMatch.class));
		TYPE_MATCHES.addAll(CLASS_MATCHES.stream().filter(aop -> aop.classMatch() != null).map(IEnhanceClassMatch::classMatch).collect(Collectors.toList()));
		LOG.info("match class:{}", TYPE_MATCHES);
		IGNORE_MATCHES.addAll(getOrRegexMatch(Config.IGNORE_CLASS.getValue()));
    	LOG.info("ignore class:{}", IGNORE_MATCHES);
		IGNORE_METHOD_MATCHES.addAll(getOrRegexMatch(Config.IGNORE_METHODS.getValue()));
    	LOG.info("ignore method:{}", IGNORE_METHOD_MATCHES);
    }

	/**
	 * 用于类加载后新增增强匹配
	 * @param matcher	动态Matcher
	 */
	public static void addTransformerMatchers(InterceptorMatcher matcher) {
		TRANSFORMER_MATCHERS.add(matcher);
		if(matcher.match() != null) {
			TYPE_MATCHES.add(matcher.match());
		}
	}
    
    private static InterceptorMatcher getMatcherProxy(InterceptorMatcher matcher) {
    	Entry<Class<?>, Class<?>> proxy = MatchProxyManage.getProxy(matcher.getClass());
    	if(proxy == null) {
    		return matcher;
    	}
    	try {
    		InterceptorMatcher proxyMatcher = (InterceptorMatcher) proxy.getValue().getConstructor(proxy.getKey()).newInstance(matcher);
			InstanceManage.registryInit(proxyMatcher);
    		LOG.info("{} proxy {}", matcher.getClass(),proxyMatcher.getClass());
    		return proxyMatcher;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			LOG.warn(e, "Convert proxy exception：{}", matcher.getClass());
			return matcher;
		}
    }

    private static List<IConfigMatch> getOrRegexMatch(Set<String> configs) {
    	List<IConfigMatch> matches = new ArrayList<>();
    	if(configs != null) {
    		for(String s : configs) {
    			matches.add(new MethodRegexMatch(s));
    		}
    	}
    	return matches;
    }
    
    /**
     *	 注册关闭通知，注意关闭应用不能使用kill -9，会导致下面的方法不执行
     */
    private static void addShutdownHook() {
    	Runtime.getRuntime().addShutdownHook(new Thread(InstanceManage::afterStop));
    }
    
}
