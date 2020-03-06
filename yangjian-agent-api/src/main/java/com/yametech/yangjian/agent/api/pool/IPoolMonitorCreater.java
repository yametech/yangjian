package com.yametech.yangjian.agent.api.pool;

import com.yametech.yangjian.agent.api.IMetricMatcher;

/**
 * 
 * @Description 实现类用于完成池监控实例创建
 * 
 */
public interface IPoolMonitorCreater extends IMetricMatcher {
	
	@Override
	default String type() {
		return "default";
	}
}
