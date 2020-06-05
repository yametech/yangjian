package com.yametech.yangjian.agent.core.metric.consume;

import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.convert.statistic.StatisticType;
import com.yametech.yangjian.agent.api.convert.statistic.impl.BaseStatistic;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class SecondStatisticBean {
	private static final ILogger LOG = LoggerFactory.getLogger(SecondStatisticBean.class);
	private static final Integer IDENTIFY_MAX_SIZE = 10000;// identify最大允许的个数
	private static final Integer IDENTIFY_MAX_LENGTH = 300;// identify最大允许的长度
	private long second;
	private Map<String, Map<String, Map<StatisticType, BaseStatistic>>> statistics = new HashMap<>();
	
	public SecondStatisticBean(long second) {
		this.second = second;
	}
	
	public Long getSecond() {
		return second;
	}
	
	public void statistic(TimeEvent timeEvent) {
		String identify = timeEvent.getIdentify().length() > IDENTIFY_MAX_LENGTH ? timeEvent.getIdentify().substring(0, IDENTIFY_MAX_LENGTH) : timeEvent.getIdentify();
		Map<String, Map<StatisticType, BaseStatistic>> identifyMap =  statistics.computeIfAbsent(timeEvent.getType(), key -> new HashMap<>());
//		Map<StatisticType, BaseStatistic> statisticTypeMap = identifyMap.computeIfAbsent(timeEvent.getIdentify(), key -> new EnumMap<>(StatisticType.class));
		Map<StatisticType, BaseStatistic> statisticTypeMap = identifyMap.get(identify);
		if(statisticTypeMap == null) {
			if(identifyMap.size() > IDENTIFY_MAX_SIZE) {
				LOG.warn("忽略过多的统计标识：{}", timeEvent);
				return;
			}
			statisticTypeMap = new EnumMap<>(StatisticType.class);
			identifyMap.put(identify, statisticTypeMap);
		}
		
		for(StatisticType type : timeEvent.getStatisticTypes()) {
			BaseStatistic secondStatistic = statisticTypeMap.get(type);
			if(secondStatistic == null) {
				try {
					secondStatistic = type.getStatistic();
					secondStatistic.reset(timeEvent.getType(), identify, second);
					statisticTypeMap.put(type, secondStatistic);
				} catch (InstantiationException | IllegalAccessException e) {
					LOG.warn(e, "create baseStatistic instance exception: {}", type);
					continue;
				}
			}
			secondStatistic.combine(timeEvent);
		}
	}
	
	public Map<String, Map<String, Map<StatisticType, BaseStatistic>>> getStatistics() {
		return statistics;
	}
	
}
