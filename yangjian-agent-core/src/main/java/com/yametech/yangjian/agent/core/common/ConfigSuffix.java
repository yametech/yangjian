package com.yametech.yangjian.agent.core.common;

public enum ConfigSuffix {
	METRIC("metric"),
	TRACE("trace"),
	SUBCRIBE_EVENT("subcribeEvent"),
	REPORT("report");
	
	private String suffix;
	private ConfigSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public String getSuffix() {
		return suffix;
	}
}
