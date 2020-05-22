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
package com.yametech.yangjian.agent.api.common;

import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.api.IConfigLoader;
import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IWeight;
import com.yametech.yangjian.agent.api.bean.ConfigNotifyType;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class InstanceManage {
	private static final ILogger LOG = LoggerFactory.getLogger(InstanceManage.class);
	public static final Object EMPTY_VALUE = new Object();
	private static Map<Class<?>, Object> spiInstances = new ConcurrentHashMap<>();// 加载的spi实例
    private static Set<Object> loadedInstance = new CopyOnWriteArraySet<>();// 需要托管的实例
    
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
	 *
	 *
	 * 按照Class获取对应Class单个实例
	 * @param cls	实例类型
	 * @param <T>	返回类型
	 * @return	cls的实例
	 */
	public static <T> T getInstance(Class<T> cls) {
		List<T> instances = listInstance(cls);
		if(!instances.isEmpty()) {
			return instances.get(0);
		}
		return null;
	}

	/**
	 *
	 * 按照Class获取对应Class实例列表
	 * @param cls	实例类型
	 * @param <T>	类型
	 * @return	cls的实例列表
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> listInstance(Class<T> cls) {
		loadSpiInstance(cls);// 初始化对应的SPI实例
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
	 * @return	所有实现SPI的实例
	 */
//	public static List<SPI> getSpis() {
//		return loadedInstance.stream().filter(instance -> instance instanceof SPI)
//				.map(instance -> (SPI)instance)
//				.collect(Collectors.toList());
//	}

	/**
	 * 删除禁用的SPI
	 * @param spiCls	删除的spi类
	 */
	public static void removeSpi(Class<?> spiCls) {
		spiInstances.remove(spiCls);
	}
	
	public static void addSpi(Class<?> spiCls) {
		spiInstances.put(spiCls, EMPTY_VALUE);
	}
	
	/**
	 * @return	是否为需要初始化的实例
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
	 * 如果实例需要init则注册，降低托管实例数
	 * @param instance	需要初始化的实例
	 * @return	是否执行init
	 */
	public static boolean registryInit(Object instance) {
		if(!isInitInstance(instance)) {
			return false;
		}
		return registry(instance);
	}

	/**
	 * @param instance	 注册一个托管实例
	 * @return	是否注册成功
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
	
	private static <T> void init(Class<T> cls, Consumer<List<T>> consumer) {
		if(Boolean.TRUE.equals(initStatus.get(cls))) {
			LOG.warn("禁止重复执行");
			return;
		}
		initStatus.put(cls, true);// 要放到runnable.run()之前，防止在run的过程中调用registry时无法正常调用init；EventMatcherInit.configKeyValue就会导致这个问题
		consumer.accept(InstanceManage.listInstance(cls));
	}
	
	 /**
     * 	初始化配置
     * @param arguments	配置参数
     */
	public static synchronized void loadConfig(String arguments) {
    	for(IConfigLoader loader: InstanceManage.listInstance(IConfigLoader.class)) {
    		try {
    			loader.load(arguments);
    		} catch (Exception e) {
    			throw new RuntimeException(e);
    		}
    	}
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
		init(IConfigReader.class, instances -> instances.forEach(InstanceManage::readerInit));
	}
	
	/**
	 * 刷新配置
	 * @param config	最新获取的配置信息
	 */
	public static void refreshConfig(Map<String, String> config) {
		if(config == null) {
			return;
		}
		Map<String, String> allConfig = Config.defaultConfig();
		allConfig.putAll(config);
		for (IConfigReader configReader : listInstance(IConfigReader.class)) {
			if(configReader.notifyType().equals(ConfigNotifyType.ONCE)) {
				continue;
			}
			Set<String> keys = getReaderConfigKey(configReader);
			Map<String, String> newKVs = new HashMap<>();
			for (Entry<String, String> entry : allConfig.entrySet()) {
				boolean match = keys.stream().anyMatch(keyRegex -> Pattern.matches(keyRegex, entry.getKey()));
				if (match) {
					newKVs.put(entry.getKey(), entry.getValue());
				}
			}
			if(configReader.notifyType().equals(ConfigNotifyType.ALWAYS)) {
				configReader.configKeyValue(newKVs);
				continue;
			}
			if(configReader.notifyType().equals(ConfigNotifyType.CHANGE) && !mapEqual(configKVs(keys), newKVs)) {
				configReader.configKeyValue(newKVs);
			}
		}
	}
	
	/**
	 * 比较两个Map数据是否相同
	 * @param oldKVs	旧kv
	 * @param newKVs	新kv
	 * @return	是否相同
	 */
	private static boolean mapEqual(Map<String, String> oldKVs, Map<String, String> newKVs) {
		if((oldKVs == null || oldKVs.isEmpty()) && 
				(newKVs == null || newKVs.isEmpty())) {
			return true;
		}
		if(oldKVs == null || newKVs == null || oldKVs.size() != newKVs.size()) {
			return false;
		}
		for(Entry<String, String> entry : oldKVs.entrySet()) {
			if(!newKVs.containsKey(entry.getKey()) || (entry.getValue() == null && newKVs.get(entry.getKey()) != null)
					|| (entry.getValue() != null && !entry.getValue().equals(newKVs.get(entry.getKey())))) {
				return false;
			}
		}
		return true;
	}
	
	/**
     * 	初始化逻辑
     */
	public static synchronized void beforeRun() {
		init(IAppStatusListener.class, instances -> instances.forEach(InstanceManage::beforeRunInit));
    }
	
	private static void beforeRunInit(IAppStatusListener listener) {
		listener.beforeRun();
	}
	
    /**
     * 开启定时调度
     */
	public static synchronized void startSchedule() {
		init(ISchedule.class, instances -> {
			service = Executors.newScheduledThreadPool(Config.SCHEDULE_CORE_POOL_SIZE.getValue(), new CustomThreadFactory("agent-schedule", true));
			instances.forEach(schedule -> {
				if(schedule.initialDelay() == 0) {
					schedule.execute();
				}
			});// 执行一次定时任务，防止多线程类加载死锁
			instances.forEach(InstanceManage::scheduleInit);
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
	 * 单个IConfigReader实例下发配置
	 * @param configReader	下发配置的实例
	 */
	private static void readerInit(IConfigReader configReader) {
		Set<String> keys = getReaderConfigKey(configReader);
		configReader.configKeyValue(configKVs(keys));
	}
	
	private static Map<String, String> configKVs(Set<String> keys) {
		Map<String, String> kvs = new HashMap<>();
		for (String key : Config.configKeys()) {
			boolean match = keys.stream().anyMatch(keyRegex -> Pattern.matches(keyRegex, key));
			if (match) {
				kvs.put(key, Config.getKv(key));
			}
		}
		return kvs;
	}
	
	private static Set<String> getReaderConfigKey(IConfigReader configReader) {
		Set<String> keys = configReader.configKey();
		if (keys == null) {
			keys = new HashSet<>();
		}
		if (keys.isEmpty()) {// 不存在配置时，使用IConfigReader实现类类名作为key前缀查找
			String defaultConfigKeyPrefix = configReader.getClass().getSimpleName();
			keys.add(defaultConfigKeyPrefix);
			keys.add(defaultConfigKeyPrefix + "\\..*");
		}
		return keys;
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
