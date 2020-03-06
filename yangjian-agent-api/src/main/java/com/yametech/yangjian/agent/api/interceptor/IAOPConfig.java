package com.yametech.yangjian.agent.api.interceptor;

public interface IAOPConfig {
	
	/**
	 * 初始化AOP需要的配置
	 * @param config 配置对象，使用jdk自带的类携带配置，避免类加载失败问题
	 */
	default void setAOPConfig(Object config) {}
}
