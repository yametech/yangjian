package cn.ecpark.tool.agent.api;

import java.time.Duration;

import cn.ecpark.tool.agent.api.base.SPI;

public interface IAppStatusListener extends SPI {
	
	/**
	 * 配置加载完成之后执行逻辑之前时执行
	 */
	void beforeRun();

	/**
	 * 应用关闭时执行
	 */
	boolean shutdown(Duration duration);
	
}
