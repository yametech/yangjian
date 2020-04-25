package com.yametech.yangjian.agent.client;

/**
 * 状态收集接口，agent会定时调用collect收集状态，并上报到监控后端
 * @Description 
 * 
 * @author liuzhao
 * @date 2020年4月23日 下午3:13:23
 */
public interface IStatusCollect {
	
	/**
	 * 
	 * @return	返回服务运行状态字符串，长度不能超过200，如果返回null则不上报信息
	 */
	StatusReturn collect();
	
	/**
	 * 
	 * @return	状态类型，长度不能超过20，不可为null
	 */
	String type();
	
	/**
	 * collect执行间隔秒数
	 * @return	间隔秒数
	 */
	default int interval() {
		return 10;
	}
}
