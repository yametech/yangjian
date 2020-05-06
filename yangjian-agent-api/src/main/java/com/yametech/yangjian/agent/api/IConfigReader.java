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
package com.yametech.yangjian.agent.api;

import java.util.Map;
import java.util.Set;

import com.yametech.yangjian.agent.api.bean.ConfigNotifyType;

public interface IConfigReader {
	
	/**
	 * 申明需要哪些key的正则
	 * @return	key列表
	 */
	default Set<String> configKey() {
		return null;
	}
	
	/**
	 * 定义配置回调方法
	 * @param kv	配置数据
	 */
	void configKeyValue(Map<String, String> kv);
	
	/**
	 * 配置通知类型
	 * @return	ONCE：仅通知一次；CHANGE：当前实例订阅的任何一个key有变更，则订阅的全部key-value都通知一次；ALWAYS：可接收重复通知；
	 */
	default ConfigNotifyType notifyType() {
		return ConfigNotifyType.ONCE;
	}
	
	/**
	 * 获取Int型配置值
	 * @param configValue
	 * @param defaultValue
	 * @return
	 */
	public static Integer getIntValue(String configValue, Integer defaultValue) {
		if(configValue == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(configValue);
        } catch(Exception e) {
        	return defaultValue;
        }
	}
	
}
