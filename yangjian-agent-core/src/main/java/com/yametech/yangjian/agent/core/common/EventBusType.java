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
