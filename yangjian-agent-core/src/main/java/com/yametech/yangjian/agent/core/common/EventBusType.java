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

package com.yametech.yangjian.agent.core.common;

public enum EventBusType {
	METRIC("metric"),// metric数据生产/消费量，用于日志输出标识、线程标识、配置
	TRACE("trace"),// trace数据生产/消费量，用于日志输出标识、线程标识、配置
	SUBCRIBE_EVENT("subcribeEvent"),// 方法调用事件生产/消费量，用于日志输出标识、线程标识、配置
	REPORT("report");
	
	private String metricType;
	private String configKeySuffix;
	
	private EventBusType(String configKeySuffix) {
		this(configKeySuffix, configKeySuffix);
	}
	private EventBusType(String metricType, String configKeySuffix) {
		this.metricType = metricType;
		this.configKeySuffix = configKeySuffix;
	}
	
	public String getMetricType() {
		return metricType;
	}
	
	public String getConfigKeySuffix() {
		return configKeySuffix;
	}
}
