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

package com.yametech.yangjian.agent.core.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.base.IWeight;
import com.yametech.yangjian.agent.api.base.SPI;
import com.yametech.yangjian.agent.core.config.Config;
import com.yametech.yangjian.agent.core.core.classloader.AgentClassLoader;
import com.yametech.yangjian.agent.core.log.ILogger;
import com.yametech.yangjian.agent.core.log.LoggerFactory;

public class InstanceManage {
	// 已加载的spi实例
	private static ILogger log = LoggerFactory.getLogger(InstanceManage.class);
    private static List<SPI> spis = new ArrayList<>();
//    private static ScheduledExecutorService service;
    
	/**
	 * 加载所有的spi
	 */
	public static void loadSpi() {
//		ServiceLoader<SPI> starterLoader = ServiceLoader.load(SPI.class, AgentClassLoader.getDefault());
//		starterLoader.forEach(spis::add);
		
		List<String> spiClasses = getSPIClass();
		if(spiClasses == null) {
			return;
		}
		spiClasses.forEach(clsName -> {
			try {
				Class<?> cls = Class.forName(clsName, true, AgentClassLoader.getDefault());
				if(!SPI.class.isAssignableFrom(cls)) {
					return;
				}
//				String enableConfig = Config.getKv("spi." + cls.getSimpleName());
//				boolean enable = enableConfig == null || enableConfig.equals("enable");
//				if(!enable) {
//					log.info("disable SPI：{}", cls.getName());
//					return;
//				}
				spis.add((SPI) cls.newInstance());
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				log.warn(e, "load spi error");
			}
		});
    }
	
	/**
	 * 使用InstanceManage托管实例，以便于更方便获取实例，并且包含配置变更通知
	 * 	注意：调用时间需在配置通知前
	 * @param cls	需要创建的实例class
	 * @param paramsCls	创建实例使用的参数类型
	 * @param args	创建实例使用的参数
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends SPI> T loadInstance(Class<?> cls, Class<?>[] paramsCls, Object[] args) {
		if(cls == null || !SPI.class.isAssignableFrom(cls)) {
			throw new IllegalArgumentException("cls必须实现SPI");
		}
		Constructor<?> constructor;
		try {
			constructor = cls.getConstructor(paramsCls);
			SPI spi = (SPI) constructor.newInstance(args);
			spis.add(spi);
			return (T) spi;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static List<String> getSPIClass() {
        List<String> spiClasses = new ArrayList<>();
        try {
        	Enumeration<URL> urls = AgentClassLoader.getDefault().getResources("META-INF/services/com.yametech.yangjian.agent.api.base.SPI");
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
        	log.error("read resources failure.", e);
        }
        return null;
    }
	
	public static <T> T getSpiInstance(Class<T> cls) {
		List<T> instances = listSpiInstance(cls);
		if(!instances.isEmpty()) {
			return instances.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> listSpiInstance(Class<T> cls) {
		List<T> instances = new ArrayList<>();
		for(SPI api : spis) {
			if(cls.isAssignableFrom(api.getClass())) {
				instances.add((T) api);
			}
		}
		if(instances.size() > 1 && IWeight.class.isAssignableFrom(cls)) {
			Collections.sort(instances, (o1, o2) -> Integer.compare(((IWeight)o2).weight(), ((IWeight)o1).weight()));
		}
		return instances;
	}
	
	public static List<SPI> getSpis() {
		return spis;
	}


	/**
	 * 下发配置给各个插件，不管配置有没变化都全量通知订阅的key（这个逻辑不要改，会影响订阅者）
	 */
	public static void notifyReader() {
		for (IConfigReader spi : listSpiInstance(IConfigReader.class)) {
			Set<String> keys = spi.configKey();
			if (keys == null) {
				keys = new HashSet<>();
			}
			if (keys.isEmpty()) {// 不存在配置时，使用IConfigReader实现类类名作为key前缀查找
				String defaultConfigKeyPrefix = spi.getClass().getSimpleName();
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
			spi.configKeyValue(kvs);
		}
	}
	
	 /**
     * 初始化逻辑
     */
//    private static void beforeRun(Object appStatusListener) {
//    	if(!(appStatusListener instanceof IAppStatusListener)) {
//    		return;
//    	}
//    	((IAppStatusListener)appStatusListener).beforeRun();
//    }

    /**
     * 开启定时调度
     */
//    private static void startSchedule(Object instance) {
//    	initThreadPool();
//    	if(!(instance instanceof ISchedule)) {
//    		return;
//    	}
//    	ISchedule schedule = (ISchedule) instance;
//    	service.scheduleAtFixedRate(() -> {
//			try {
//				schedule.execute();
//			} catch(Exception e) {
//				log.warn(e, "执行定时任务异常：{}", schedule.getClass());
//			}
//    	} , 0, schedule.interval(), TimeUnit.SECONDS);
//	}
    
//    private static void initThreadPool() {
//    	if(service != null) {
//    		return;
//    	}
//    	synchronized (InstanceManage.class) {
//    		if(service != null) {
//        		return;
//        	}
//    		service = Executors.newScheduledThreadPool(Config.SCHEDULE_CORE_POOL_SIZE.getValue(), new CustomThreadFactory("schedule", true));
//		}
//    }
    
//    public static void shutdown() throws InterruptedException {
//    	service.shutdown();
//		service.awaitTermination(5, TimeUnit.SECONDS);
//    }
}
