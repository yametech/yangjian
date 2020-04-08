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
package com.yametech.yangjian.agent.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.core.core.InstanceManage;

public class Config {
	private static final List<ConfigValue<?>> CONFIG_VALUES = new ArrayList<>();
//	private static String configFilePath = System.getProperty(key, def);
    // 忽略加强类包配置，支持格式com.*.XxService，其中*可匹配任意字符（包含多层级包名）
	public static final ConfigValue<Set<String>> IGNORE_CLASS = new ConfigValue<>("ignore.enhance.classRegular", null,
    	value -> new HashSet<String>(Arrays.asList(value.split("\r\n")))
	);
    // 忽略加强类方法配置，支持格式toString()、test(java.lang.Long)、com.*.XxService.equal()
	public static final ConfigValue<Set<String>> IGNORE_METHODS = new ConfigValue<>("ignore.enhance.methodRegular", null,
    	value -> new HashSet<String>(Arrays.asList(value.split("\r\n")))
	);
    // 方法调用事件缓存大小，必须为2的倍数，需考虑内存占用
//	public static final ConfigValue<Integer> CALL_EVENT_BUFFER_SIZE = new ConfigValue<>("methodCallEvent.bufferSize", (1 << 15) + "", Integer::parseInt);
    // 定时调度线程池线程个数，定时任务使用
	public static final ConfigValue<Integer> SCHEDULE_CORE_POOL_SIZE = new ConfigValue<>("schedule.corePoolSize", 5 + "", Integer::parseInt);
	// 默认读取服务名称使用的key
//	private static final String DEFAULT_SERVICE_NAME_KEY = "service.name";
	// 从启动参数中读取服务名称使用的key，通过配置可读取skywalking配置的服务名称，避免服务接入两个agent时需要配置两次服务名称
//	private static final ConfigValue<String> SERVICE_NAME_SYSTEMKEY = new ConfigValue<>("service.name.systemKey", DEFAULT_SERVICE_NAME_KEY, String::toString);
	// 当前运行的应用名称
	public static final ConfigValue<String> SERVICE_NAME = new ConfigValue<>("service.name", "", String::toString);

    // 动态配置，key/value的配置方式，本地默认配置
    private static Map<String, String> defaultKvs = new HashMap<>();
	// 动态配置，key/value的配置方式，远程配置
    private static Map<String, String> remoteKvs = new HashMap<>();

    static {
    	CONFIG_VALUES.add(IGNORE_CLASS);
    	CONFIG_VALUES.add(IGNORE_METHODS);
//    	CONFIG_VALUES.add(CALL_EVENT_BUFFER_SIZE);
    	CONFIG_VALUES.add(SCHEDULE_CORE_POOL_SIZE);
//    	CONFIG_VALUES.add(SERVICE_NAME_SYSTEMKEY);
    	CONFIG_VALUES.add(SERVICE_NAME);
    }
    
    private Config() {}

	public static void addConfigProperties(String path) throws IOException {
		File configFile = new File(path);
		if (!configFile.exists()) {
			throw new FileNotFoundException("can not find local config file:" + path);
		}
		Properties prop = new Properties();
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
			prop.load(reader);
			prop.entrySet().forEach(entry -> {
				String key = entry.getKey() == null ? null : entry.getKey().toString();
				String value = entry.getValue() == null ? null : entry.getValue().toString();
				Config.setConfig(key, value);
			});
		}
	}

	/**
	 * 刷新远程配置（覆盖刷新）
	 *
	 * @param config
	 */
	public static void refreshRemoteConfig(Map<String, String> config) {
    	remoteKvs.clear();
		config.entrySet().forEach(entry -> Config.setConfig(entry.getKey(), entry.getValue(), false));
		InstanceManage.notifyReader();
	}

	public static void setConfig(String key, String value) {
		setConfig(key, value, true);
	}

	public static void setConfig(String key, String value, boolean setDefault) {
		if (StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {
			return;
		}
		for (ConfigValue<?> configValue : CONFIG_VALUES) {
			if (configValue.setValueByKey(key, value)) {
				break;
			}
		}
		if (setDefault) {
			defaultKvs.put(key, value);
		} else {
			remoteKvs.put(key, value);
		}
	}
    
    public static String getKv(String key) {
    	// 远程配置覆盖本地配置
    	if (remoteKvs.containsKey(key)) {
    		return remoteKvs.get(key);
		}
		return defaultKvs.get(key);
	}

	public static String getKv(String key, String def) {
		String value = getKv(key);
		return value != null ? value : def;
	}
	
	public static Set<String> configKeys() {
		return Stream.of(defaultKvs, remoteKvs)
				.flatMap(m -> m.keySet().stream())
				.collect(Collectors.toCollection(HashSet::new));
	}
	
	
}
