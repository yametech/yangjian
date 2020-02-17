package cn.ecpark.tool.agent.api.convert.statistic.impl;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;

import cn.ecpark.tool.agent.api.convert.statistic.IStatistic;
import cn.ecpark.tool.agent.api.convert.statistic.StatisticType;

/**
 * 子类必须包含
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月25日 下午2:40:27
 */
public abstract class BaseStatistic implements IStatistic {
	private String type;// 事件类型
	private String sign;// 事件唯一标识
	private long second;// 统计的秒数
	private long updateTime;// 数据最近更新事件，在数据输出时会判断该时间与当前时间的差值
	
	public String getType() {
		return type;
	}
	
	public long getSecond() {
		return second;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	
	@Override
	public String toString() {
		return type + "	" + sign + "	" + second;
	}

	@Override
	public final void reset(String type, String sign, long second) {
		this.type = type;
		this.sign = sign;
		this.second = second;
		updateTime = System.currentTimeMillis();
		clear();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final Entry<String, Object>[] kv() {
		Map<String, Object> kvs = statisticKV();
		if(kvs == null || kvs.size() == 0) {
			return null;
		}
		Entry<String, Object>[] entrys = new SimpleEntry[kvs.size() + 1];
		entrys[0] = new SimpleEntry<>("sign", sign);
		int index = 1;
		for(Entry<String, Object> entry : kvs.entrySet()) {
			entrys[index++] = new SimpleEntry<>(entry);
		}
		return entrys;
	}
	
	/**
	 * 清除状态
	 */
	protected abstract void clear();
	
	/**
	 * 统计类型
	 * @return
	 */
	public abstract StatisticType statisticType();
	
	/**
	 * 统计输出key-value
	 * @return
	 */
	public abstract Map<String, Object> statisticKV();

}
