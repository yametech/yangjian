package cn.ecpark.tool.agent.api.convert.statistic;

import cn.ecpark.tool.agent.api.convert.statistic.impl.BaseStatistic;
import cn.ecpark.tool.agent.api.convert.statistic.impl.QPSStatistic;
import cn.ecpark.tool.agent.api.convert.statistic.impl.RTStatistic;

public enum StatisticType {
	QPS(QPSStatistic.class),
	RT(RTStatistic.class);
	
	private Class<? extends BaseStatistic> cls;
	
	StatisticType(Class<? extends BaseStatistic> cls) {
		this.cls = cls;
	}
	
	// 返回对应实例
	public BaseStatistic getStatistic() throws InstantiationException, IllegalAccessException {
		return cls.newInstance();
	}
}
