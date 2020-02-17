package cn.ecpark.tool.agent.core;

import cn.ecpark.tool.agent.api.*;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.common.StringUtil;
import cn.ecpark.tool.agent.api.configmatch.CombineOrMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodRegexMatch;
import cn.ecpark.tool.agent.core.aop.MetricMatcherProxy;
import cn.ecpark.tool.agent.core.aop.base.MetricEventBus;
import cn.ecpark.tool.agent.core.config.Config;
import cn.ecpark.tool.agent.core.core.InstanceManage;
import cn.ecpark.tool.agent.core.core.agent.AgentListener;
import cn.ecpark.tool.agent.core.core.agent.AgentTransformer;
import cn.ecpark.tool.agent.core.core.classloader.AgentClassLoader;
import cn.ecpark.tool.agent.core.core.elementmatch.ClassElementMatcher;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;
import cn.ecpark.tool.agent.core.util.CustomThreadFactory;
import cn.ecpark.tool.agent.core.util.LogUtil;
import cn.ecpark.tool.agent.core.util.OSUtil;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.bytebuddy.matcher.ElementMatchers.isInterface;

public class YMAgent {
	private static ILogger log = LoggerFactory.getLogger(YMAgent.class);
	private static ScheduledExecutorService service;
	private static final String[] IGNORE_CLASS_CONFIG = new String[] {"^net\\.bytebuddy\\.", "^org\\.slf4j\\.", // ".*\\$auxiliary\\$.*", 
			"^org\\.apache\\.logging\\.", "^org\\.groovy\\.", "^sun\\.reflect\\.", // ".*javassist.*", ".*\\.asm\\..*", 这两个会有误拦截：com.alibaba.dubbo.rpc.proxy.javassist.JavassistProxyFactory
			"^org\\.apache\\.skywalking\\.", "^cn\\.ecpark\\.tool\\.agent\\."};
	private static final String[] IGNORE_METHOD_CONFIG = new String[] {".*toString\\(\\)$", ".*equals\\(java.lang.Object\\)$",
            ".*hashCode\\(\\)$", ".*clone\\(\\).*"};
	
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
    	AgentClassLoader.initDefaultLoader();
    	InstanceManage.loadSpi();
    	loadConfig(arguments);
    	InstanceManage.notifyReader();
    	beforeRun();
    	instrumentation(instrumentation);
        // 埋点日志，不允许删除
    	startSchedule();
    	addShutdownHook();
        LogUtil.println("status/start");
    }
    
    private static void instrumentation(Instrumentation instrumentation) {
    	List<InterceptorMatcher> interceptorMatchers = InstanceManage.listSpiInstance(InterceptorMatcher.class).stream().collect(Collectors.toList());
    	List<IConfigMatch> matches = interceptorMatchers.stream().filter(aop -> aop.match() != null)
    			.map(InterceptorMatcher::match).collect(Collectors.toList());
    	interceptorMatchers = interceptorMatchers.stream().map(matcher -> matcher instanceof IMetricMatcher ?
    			new MetricMatcherProxy((IMetricMatcher) matcher, InstanceManage.getSpiInstance(MetricEventBus.class)) : matcher
    			).collect(Collectors.toList());// 转换IMetricMatcher为MetricMatcherProxy
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
    	InstanceManage.listSpiInstance(ISchedule.class).forEach(schedule -> 
    		service.scheduleAtFixedRate(() -> {
    			try {
    				schedule.execute();
    			} catch(Exception e) {
    				log.warn(e, "执行定时任务异常：{}", schedule.getClass());
    			}
		} , schedule.initialDelay(), schedule.interval(), schedule.timeUnit()));
	}
    
    /**
     * 注册关闭通知
     */
    private static void addShutdownHook() {
    	Runtime.getRuntime().addShutdownHook(new Thread() {// 注意关闭应用不能使用kill -9，会导致下面的方法不执行
            @Override
    		public void run() {
            	// 埋点日志，不允许删除
            	LogUtil.println("status/shutdown");
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
            	LogUtil.println("status/closed");
            }
        });
    }
    
}
