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
package com.yametech.yangjian.agent.core.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.api.IConfigLoader;
import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IWeight;
import com.yametech.yangjian.agent.api.base.SPI;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.config.Config;
import com.yametech.yangjian.agent.core.core.classloader.AgentClassLoader;
import com.yametech.yangjian.agent.util.CustomThreadFactory;

public class InstanceManage {
	private static final ILogger LOG = LoggerFactory.getLogger(InstanceManage.class);
	private static final Object EMPTY_VALUE = new Object();
	private static Map<Class<?>, Object> spiInstances = new ConcurrentHashMap<>();// 加载的spi实例
    private static Set<Object> loadedInstance = new CopyOnWriteArraySet<>();// 需要托管的实例
    private static final String SPI_BASE_PATH = "META-INF/services/";
    private static final int MAX_INSTANCE = 2000;// 最大托管的实例个数
    private static ScheduledExecutorService service;
    private static Map<Class<?>, Boolean> initStatus = new ConcurrentHashMap<>();

    private InstanceManage() {}
    
    static {
//    	initStatus.put(IConfigLoader.class, false);
    	initStatus.put(IConfigReader.class, false);
    	initStatus.put(IAppStatusListener.class, false);
    	initStatus.put(ISchedule.class, false);
    }
    
	/**
	 * 读取所有的spi class
	 */
 	private static void loadSpi() {
		List<String> spiClasses = getSpiClass(SPI.class);
		if(spiClasses == null) {
			return;
		}
		spiClasses.forEach(clsName -> {
			try {
				Class<?> cls = Class.forName(clsName, false, AgentClassLoader.getDefault());
				if(!SPI.class.isAssignableFrom(cls)) {
					return;
				}
				spiInstances.put(cls, EMPTY_VALUE);
			} catch (ClassNotFoundException e) {
				LOG.warn(e, "load spi error");
			}
		});
    }
	
	public static List<String> getSpiClass(Class<?> cls) {
        List<String> spiClasses = new ArrayList<>();
        try {
        	Enumeration<URL> urls = AgentClassLoader.getDefault().getResources(SPI_BASE_PATH + cls.getName());
            while (urls.hasMoreElements()) {
            	URL url = urls.nextElement();
            	if("file".equals(url.getProtocol())) {// 开发环境，在ecpark-agent调试时会重复加载，所以使用protocol过滤
            		continue;
            	}
            	try (InputStream input = url.openStream()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String pluginDefine = null;
                    while ((pluginDefine = reader.readLine()) != null) {
                        if (pluginDefine.trim().length() == 0 || pluginDefine.startsWith("#")) {
                            continue;
                        }
                        spiClasses.add(pluginDefine);
                    }
                }
            }
            return spiClasses;
        } catch (IOException e) {
        	LOG.error("read resources failure.", e);
        }
        return null;
    }
	
	/**
	 * 按照Class获取对应Class单个实例
	 * @param cls
	 * @return
	 */
	public static <T> T getInstance(Class<T> cls) {
		List<T> instances = listInstance(cls);
		if(!instances.isEmpty()) {
			return instances.get(0);
		}
		return null;
	}
	
	/**
	 * 按照Class获取对应Class实例列表
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> listInstance(Class<T> cls) {
		List<T> instances = new ArrayList<>();
		for(Object api : loadedInstance) {
			if(cls.isAssignableFrom(api.getClass())) {
				instances.add((T) api);
			}
		}
		if(instances.size() > 1 && IWeight.class.isAssignableFrom(cls)) {
			Collections.sort(instances, (o1, o2) -> Integer.compare(((IWeight)o2).weight(), ((IWeight)o1).weight()));
		}
		return instances;
	}
	
	/**
	 * 获取所有实现SPI的实例
	 * @return
	 */
	public static List<SPI> getSpis() {
		return loadedInstance.stream().filter(instance -> instance instanceof SPI)
				.map(instance -> (SPI)instance)
				.collect(Collectors.toList());
	}

	/**
	 * 禁用SPI
	 *
	 * @return
	 */
	public static void removeSpi(Class<?> spiCls) {
		spiInstances.remove(spiCls);
	}
	
	/**
	 * 是否为需要初始化的实例
	 * @return
	 */
	private static boolean isInitInstance(Object obj) {
		for(Class<?> cls : initStatus.keySet()) {
			if(cls.isAssignableFrom(obj.getClass())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 注册需要init的实例，如果不需要init，则不注册
	 * @param instance
	 * @return
	 */
	public static boolean registryInit(Object instance) {
		if(!isInitInstance(instance)) {
			return false;
		}
		return registry(instance);
	}
	
	/**
	 * 注册一个托管实例
	 * @param spi
	 */
	public static boolean registry(Object instance) {
		return registry(instance, true);
	}
	
	/**
	 * 
	 * @param instance	托管实例
	 * @param reload	true：注册时执行一次初始化（用于在premain init之后托管的实例）；false：不执行初始化（用于在premain init之前托管的实例）；
	 */
	private static synchronized boolean registry(Object instance, boolean reload) {
		if(instance == null) {
			return false;
		}
		if(loadedInstance.size() > MAX_INSTANCE) {
			LOG.warn("注册实例失败，管理的实例个数已超过{}", MAX_INSTANCE);
			return false;
		}
		if(loadedInstance.contains(instance)) {
			return true;
		}
		loadedInstance.add(instance);
		if(!reload) {
			return false;
		}
//		if(instance instanceof IConfigLoader && Boolean.TRUE.equals(initStatus.get(IConfigLoader.class))) {
//			loaderInit((IConfigLoader)instance, arguments);
//		}
		if(instance instanceof IConfigReader && Boolean.TRUE.equals(initStatus.get(IConfigReader.class))) {
			readerInit((IConfigReader)instance);
		}
		if(instance instanceof IAppStatusListener && Boolean.TRUE.equals(initStatus.get(IAppStatusListener.class))) {
			beforeRunInit((IAppStatusListener)instance);
		}
		if(instance instanceof ISchedule && Boolean.TRUE.equals(initStatus.get(ISchedule.class))) {
			scheduleInit((ISchedule)instance);
		}
		return true;
	}
	
	/**
	 * 注册IConfigReader实例，并执行一次配置通知
	 * @param configReader
	 */
//	public static void registryConfigReaderInstance(IConfigReader configReader) {
//		registryConfigReaderInstance(configReader, true);
//	}
	/**
	 * 注册IConfigReader实例，根据needNotify确认是否执行通知
	 * @param configReader
	 * @param needNotifyConfig	true：注册时执行一次配置通知（用于在全局通知(InstanceManage.notifyReaders)之后调用该方法）；false：不执行配置通知（用于在全局通知之前调用该方法）；
	 */
//	public static void registryConfigReaderInstance(IConfigReader configReader, boolean needNotifyConfig) {
//		loadedInstance.add(configReader);
//		if(needNotifyConfig) {
//			readerInit(configReader);
//		}
//	}
	
	private static void init(Class<?> cls, Runnable runnable) {
		if(Boolean.TRUE.equals(initStatus.get(cls))) {
			LOG.warn("禁止重复执行");
			return;
		}
		initStatus.put(cls, true);// 要放到runnable.run()之前，防止在run的过程中调用registry时无法正常调用init；EventMatcherInit.configKeyValue就会导致这个问题
		runnable.run();
	}
	
	 /**
     * 	初始化配置
     * @throws Exception 
     */
	public static synchronized void loadConfig(String arguments) {
		loadSpi();
		loadSpiInstance(IConfigLoader.class);
    	for(IConfigLoader loader: InstanceManage.listInstance(IConfigLoader.class)) {
    		try {
    			loader.load(arguments);
    		} catch (Exception e) {
    			throw new RuntimeException(e);
    		}
    	}
    	loadSpiInstance(null);// 等IConfigLoader实例加载完配置再初始化其他spi实例，避免其他spi实例创建时使用到配置
    }
	
	public static Set<Class<?>> listSpiClass() {
		return new HashSet<>(spiInstances.keySet());
	}
	
	private static synchronized void loadSpiInstance(Class<?> spiCls) {
		spiInstances.entrySet().forEach(entry -> {
			if(entry.getValue() != EMPTY_VALUE) {
				return;
			}
			if(spiCls != null && !spiCls.isAssignableFrom(entry.getKey())) {
				return;
			}
			try {
				Object spi = entry.getKey().newInstance();
				entry.setValue(spi);
				registry(spi, false);
			} catch (InstantiationException | IllegalAccessException e) {
				LOG.warn(e, "load spi instance error:{}", entry.getKey());
			}
		});
	}
	
	/**
	 * 下发配置给各个插件，不管配置有没变化都全量通知订阅的key（这个逻辑不要改，会影响订阅者）
	 */
	public static synchronized void notifyReader() {
		init(IConfigReader.class, () -> {
			for (IConfigReader configReader : listInstance(IConfigReader.class)) {
				readerInit(configReader);
			}
		});
	}
	
	/**
     * 	初始化逻辑
     */
	public static synchronized void beforeRun() {
		init(IAppStatusListener.class, () -> InstanceManage.listInstance(IAppStatusListener.class).forEach(InstanceManage::beforeRunInit));
    }
	
	private static void beforeRunInit(IAppStatusListener listener) {
		listener.beforeRun();
	}
	
    /**
     * 开启定时调度
     */
	public static synchronized void startSchedule() {
		init(ISchedule.class, () -> {
			service = Executors.newScheduledThreadPool(Config.SCHEDULE_CORE_POOL_SIZE.getValue(), new CustomThreadFactory("agent-schedule", true));
			InstanceManage.listInstance(ISchedule.class).forEach(schedule -> {
				if(schedule.initialDelay() == 0) {
					schedule.execute();
				}
			});// 执行一次定时任务，防止多线程类加载死锁
			InstanceManage.listInstance(ISchedule.class).forEach(InstanceManage::scheduleInit);
		});
	}
	
	private static void scheduleInit(ISchedule schedule) {
		int delay = schedule.initialDelay();
		if(delay == 0) {
			delay += schedule.interval();
		}
		service.scheduleAtFixedRate(() -> {
			try {
				schedule.execute();
			} catch(Exception e) {
				LOG.warn(e, "执行定时任务异常：{}", schedule.getClass());
			}
		} , delay, schedule.interval(), schedule.timeUnit());
	}
    
	/**
	 * 单个IConfigReader实例下发（刷新）配置
	 * @param configReader
	 */
	private static void readerInit(IConfigReader configReader) {
		Set<String> keys = configReader.configKey();
		if (keys == null) {
			keys = new HashSet<>();
		}
		if (keys.isEmpty()) {// 不存在配置时，使用IConfigReader实现类类名作为key前缀查找
			String defaultConfigKeyPrefix = configReader.getClass().getSimpleName();
			keys.add(defaultConfigKeyPrefix);
			keys.add(defaultConfigKeyPrefix + "\\..*");
		}
		Map<String, String> kvs = new HashMap<>();
		for (String key : Config.configKeys()) {
			boolean match = keys.stream().anyMatch(keyRegex -> Pattern.matches(keyRegex, key));
			if (match) {
				kvs.put(key, Config.getKv(key));
			}

		}
		configReader.configKeyValue(kvs);
	}
	
	/**
	 * 关闭通知
	 */
	public static void afterStop() {
		List<IAppStatusListener> shutdowns = InstanceManage.listInstance(IAppStatusListener.class);
    	Collections.reverse(shutdowns);// 启动时顺序init，关闭时倒序showdown
    	for(IAppStatusListener spi : shutdowns) {
    		long startMillis = System.currentTimeMillis();
    		try {
    			boolean success = spi.shutdown(Duration.ofSeconds(10));// TODO 此处增加异步关闭，避免同步关闭时耗时太久
    			if(!success) {
    				LOG.warn("执行关闭逻辑失败：{}", spi);
    			}
    		} catch (Exception e) {
    			LOG.warn(e, "关闭服务异常，可能丢失数据");
    		} finally {
				LOG.info("{}关闭耗时：{}毫秒", spi.getClass(), System.currentTimeMillis() - startMillis);
			}
    	}
    	service.shutdown();
    	try {
    		service.awaitTermination(5, TimeUnit.SECONDS);
    	} catch (InterruptedException e) {
    		LOG.warn(e, "关闭定时任务被打断");
    		Thread.currentThread().interrupt();
    	}
	}
	
}
