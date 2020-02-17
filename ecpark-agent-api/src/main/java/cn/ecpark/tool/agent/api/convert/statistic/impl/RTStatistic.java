package cn.ecpark.tool.agent.api.convert.statistic.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.convert.statistic.StatisticType;

public class RTStatistic extends BaseStatistic {
	private LongAdder num = new LongAdder();// 当前秒数的总调用次数
	private LongAdder total = new LongAdder();// 当前秒数的总耗时（毫秒）
	private AtomicLong min = new AtomicLong(-1);// 当前秒数的最小耗时（毫秒）
	private AtomicLong max = new AtomicLong(-1);// 当前秒数的最大耗时（毫秒）

	@Override
	public void combine(TimeEvent timeEvent) {
		this.num.add(timeEvent.getNumber());
		this.total.add(timeEvent.getUseTime());
		long useTimeEach = timeEvent.getUseTime() / timeEvent.getNumber();
		setMax(useTimeEach);
		setMin(useTimeEach);
	}
	
	@Override
	protected void clear() {
		num.reset();
		total.reset();
		min.set(-1);
		max.set(-1);
	}
	
	@Override
	public Map<String, Object> statisticKV() {
		Map<String, Object> kvs = new HashMap<>();
		kvs.put("num", num.sum());
		kvs.put("rt_total", total.sum());
		kvs.put("rt_min", min.get());
		kvs.put("rt_max", max.get());
		return kvs;
	}
	
	@Override
	public StatisticType statisticType() {
		return StatisticType.RT;
	}

	private void setMin(long min) {
		while(true) {
			long currentValue = this.min.get();
			if((currentValue != -1 && currentValue <= min) 
					|| this.min.compareAndSet(currentValue, min)) {
				break;
			}
		}
	}

	private void setMax(long max) {
		while(true) {
			long currentValue = this.max.get();
			if((currentValue != -1 && currentValue > max) 
					|| this.max.compareAndSet(currentValue, max)) {
				break;
			}
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + " : " + num.sum() + "	" + total.sum() + "	" + min.get() + "	" + max.get();
	}

}
