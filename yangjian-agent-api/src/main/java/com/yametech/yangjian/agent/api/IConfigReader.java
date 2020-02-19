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

package com.yametech.yangjian.agent.api;

import java.util.Map;
import java.util.Set;

import com.yametech.yangjian.agent.api.base.IWeight;
import com.yametech.yangjian.agent.api.base.SPI;

public interface IConfigReader extends IWeight, SPI {
	
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
	
}
