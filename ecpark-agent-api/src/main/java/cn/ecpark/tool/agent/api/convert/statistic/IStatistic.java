package cn.ecpark.tool.agent.api.convert.statistic;

import java.util.Map.Entry;

import cn.ecpark.tool.agent.api.bean.TimeEvent;

public interface IStatistic {
	
	/**
	 * 合并数据
	 * @param timeEvent
	 */
	void combine(TimeEvent timeEvent);
	
	/**
	 * 重置数据
	 * @param type
	 * @param identify
	 * @param second
	 */
	void reset(String type, String identify, long second);
	
	/**
	 * 返回当前统计的数据key-value
	 * @return
	 */
	Entry<String, Object>[] kv();
}
