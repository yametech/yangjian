package cn.ecpark.tool.agent.api;

import java.util.Map;
import java.util.Set;

import cn.ecpark.tool.agent.api.base.IWeight;
import cn.ecpark.tool.agent.api.base.SPI;

public interface IConfigReader extends IWeight, SPI {
	
	/**
	 * 申明需要哪些key的正则
	 * @return
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
