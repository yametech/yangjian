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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.base.IWeight;
import com.yametech.yangjian.agent.api.base.SPI;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.config.Config;
import com.yametech.yangjian.agent.core.core.classloader.AgentClassLoader;

public class InstanceManage {
	private static ILogger log = LoggerFactory.getLogger(InstanceManage.class);
    private static List<Object> spis = new ArrayList<>();// 已加载的spi实例
    private static final String SPI_PATH = "META-INF/services/com.yametech.yangjian.agent.api.base.SPI";

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
				spis.add(cls.newInstance());
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				log.warn(e, "load spi error");
			}
		});
    }
	
	private static List<String> getSPIClass() {
        List<String> spiClasses = new ArrayList<>();
        try {
        	Enumeration<URL> urls = AgentClassLoader.getDefault().getResources(SPI_PATH);
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
	
	/**
	 * 按照Class获取对应Class单个实例
	 * @param cls
	 * @return
	 */
	public static <T> T getSpiInstance(Class<T> cls) {
		List<T> instances = listSpiInstance(cls);
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
	public static <T> List<T> listSpiInstance(Class<T> cls) {
		List<T> instances = new ArrayList<>();
		for(Object api : spis) {
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
		return spis.stream().filter(instance -> instance instanceof SPI)
				.map(instance -> (SPI)instance)
				.collect(Collectors.toList());
	}

	/**
	 * 移除指定SPI实例
	 *
	 * @return
	 */
	public static boolean removeSpi(SPI spi) {
		return spis.remove(spi);
	}

	/**
	 * 注册IConfigReader实例，并执行一次配置通知
	 * @param configReader
	 */
	public static void registryConfigReaderInstance(IConfigReader configReader) {
		registryConfigReaderInstance(configReader, true);
	}
	/**
	 * 注册IConfigReader实例，根据needNotify确认是否执行通知
	 * @param configReader
	 * @param needNotifyConfig	true：注册时执行一次配置通知（用于在全局通知(InstanceManage.notifyReaders)之后调用该方法）；false：不执行配置通知（用于在全局通知之前调用该方法）；
	 */
	public static void registryConfigReaderInstance(IConfigReader configReader, boolean needNotifyConfig) {
		spis.add(configReader);
		if(needNotifyConfig) {
			notifyReader(configReader);
		}
	}
	
	/**
	 * 下发配置给各个插件，不管配置有没变化都全量通知订阅的key（这个逻辑不要改，会影响订阅者）
	 */
	public static void notifyReader() {
		for (IConfigReader configReader : listSpiInstance(IConfigReader.class)) {
			notifyReader(configReader);
		}
	}
	
	/**
	 * 单个IConfigReader实例下发（刷新）配置
	 * @param configReader
	 */
	private static void notifyReader(IConfigReader configReader) {
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
}
