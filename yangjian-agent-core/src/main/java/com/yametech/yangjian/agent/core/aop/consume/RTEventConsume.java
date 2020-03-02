/**
 * Copyright 2020 yametech.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yametech.yangjian.agent.core.aop.consume;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.convert.statistic.StatisticType;
import com.yametech.yangjian.agent.api.convert.statistic.impl.BaseStatistic;
import com.yametech.yangjian.agent.core.aop.base.ConvertTimeEvent;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.util.eventbus.consume.BaseConsume;

public class RTEventConsume implements BaseConsume<ConvertTimeEvent> {
	private static final ILogger log = LoggerFactory.getLogger(RTEventConsume.class);
	private static final int STATISTICS_SECOND_SIZE = 1 << 5;// 每种类型的统计，内存中存放的最大统计秒数个数
	private Map<String, Map<String, Map<StatisticType, BaseStatistic[]>>> allStatistics = new HashMap<>();
	private long totalNum = 0;// 总消费量
	private AtomicLong periodTotalNum = new AtomicLong(0);// 最近一个输出周期产生的事件量

	@Override
	public boolean test(ConvertTimeEvent event) {
		totalNum++;
		periodTotalNum.getAndIncrement();
		return true;
	}

	@Override
	public void accept(ConvertTimeEvent event) {
		if(event.getConvert() == null) {
			consume(event);
			return;
		}
		List<TimeEvent> timeEvents = event.getConvert().convert(event.getData());
		if (timeEvents != null) {
			timeEvents.parallelStream()
					.filter(Objects::nonNull)
					.forEach(timeEvent -> {
						// 默认优先使用timeEvent自定义的type
						if (StringUtil.isEmpty(timeEvent.getType())) {
							timeEvent.setType(event.getType());
						}
						consume(timeEvent);
					});
		}
	}

	/**
	 * 在RTEventListener中配置了一个当前实例仅被一个线程调用，所以此处的consume为线程安全的
	 * @param timeEvent
	 */
	public void consume(TimeEvent timeEvent) {
		if((System.currentTimeMillis() - timeEvent.getEventTime()) / 1000 >= STATISTICS_SECOND_SIZE) {// 数据时间超过缓存长度，丢弃
			log.warn("discard delay timeEvent: {}", timeEvent);
			return;
		}

		Map<String, Map<StatisticType, BaseStatistic[]>> typeStatistics = allStatistics.get(timeEvent.getType());
		if(typeStatistics == null) {
			typeStatistics = new HashMap<>();
			allStatistics.put(timeEvent.getType(), typeStatistics);
		}
		Map<StatisticType, BaseStatistic[]> identifyStatistics = typeStatistics.get(timeEvent.getIdentify());
		if(identifyStatistics == null) {
			identifyStatistics = new EnumMap<>(StatisticType.class);
			typeStatistics.put(timeEvent.getIdentify(), identifyStatistics);
		}

		for(StatisticType type : timeEvent.getStatisticTypes()) {
			BaseStatistic[] statistics = identifyStatistics.get(type);
			if(statistics == null) {
				statistics = new BaseStatistic[STATISTICS_SECOND_SIZE];
				identifyStatistics.put(type, statistics);
			}
			long second = timeEvent.getEventTime() / 1000;
			int index = (int) (second & (STATISTICS_SECOND_SIZE - 1));
			BaseStatistic secondStatistics = statistics[index];
			if(secondStatistics == null) {
				try {
					secondStatistics = type.getStatistic();
				} catch (InstantiationException | IllegalAccessException e) {
					log.warn(e, "创建BaseStatistic实例异常{}", type);
					continue;
				}
				secondStatistics.reset(timeEvent.getType(), timeEvent.getIdentify(), second);
				statistics[index] = secondStatistics;
			} else if(secondStatistics.getSecond() != second) {// 大部分数据不会走这里
//				if(secondStatistics.getTotal().sum() != 0) {
//					log.warn("statistics cover:{} - {}", timeEvent.getIdentify(), secondStatistics);
//				}
				secondStatistics.reset(timeEvent.getType(), timeEvent.getIdentify(), second);
			}

			secondStatistics.setUpdateTime(System.currentTimeMillis());
			secondStatistics.combine(timeEvent);
		}
	}

	/**
	 * 获取当前消费时间之前的统计值，这些历史统计值是不变的
	 * @return
	 */
	public List<BaseStatistic> getReportStatistics() {
		List<BaseStatistic> reportStatistic = new ArrayList<>();
		for(Entry<String, Map<String, Map<StatisticType, BaseStatistic[]>>> entryType : allStatistics.entrySet()) {
			for(Entry<String, Map<StatisticType, BaseStatistic[]>> entrySign : entryType.getValue().entrySet()) {
				for(Entry<StatisticType, BaseStatistic[]> entry : entrySign.getValue().entrySet()) {
					for(int i = 0; i < entry.getValue().length; i++) {
						BaseStatistic statistics = entry.getValue()[i];
						if(statistics != null && System.currentTimeMillis() - statistics.getUpdateTime() > 1000) {// 统计值所在时间秒数已不再更新或者最近处理事件的事件距离当前事件已超过1秒
							entry.getValue()[i] = null;// 重置，下次不会再取到
							reportStatistic.add(statistics);
						}
					}
				}
			}
		}
		return reportStatistic;
	}

	public long getTotalNum() {
		return totalNum;
	}

	public long getPeriodTotalNum() {
		return periodTotalNum.getAndSet(0);
	}

}
