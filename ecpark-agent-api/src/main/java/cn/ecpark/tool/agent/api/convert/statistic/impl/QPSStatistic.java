package cn.ecpark.tool.agent.api.convert.statistic.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.convert.statistic.StatisticType;

public class QPSStatistic extends BaseStatistic {
	private LongAdder num = new LongAdder();// 当前秒数的总调用次数
	
	@Override
	protected void clear() {
		num.reset();
	}
	
	@Override
	public void combine(TimeEvent timeEvent) {
		this.num.add(timeEvent.getNumber());
	}
	
	@Override
	public Map<String, Object> statisticKV() {
		Map<String, Object> kvs = new HashMap<>();
		kvs.put("num", num.sum());
		return kvs;
	}
	
	@Override
	public StatisticType statisticType() {
		return StatisticType.QPS;
	}
	
	@Override
	public String toString() {
		return super.toString() + " : " + num.sum();
	}

}
