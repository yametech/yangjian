/*
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
package com.yametech.yangjian.agent.core.metric.consume;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.convert.statistic.StatisticType;
import com.yametech.yangjian.agent.api.convert.statistic.impl.BaseStatistic;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.metric.base.ConvertTimeEvent;
import com.yametech.yangjian.agent.util.eventbus.consume.BaseConsume;

public class RTEventConsume implements BaseConsume<ConvertTimeEvent> {
	private static final ILogger log = LoggerFactory.getLogger(RTEventConsume.class);
	private static final int STATISTICS_SECOND_SIZE = 1 << 3;// 每种类型的统计，内存中存放的最大统计秒数个数
	private SecondStatisticBean[] allStatistics = new SecondStatisticBean[STATISTICS_SECOND_SIZE];
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
		if (timeEvents == null) {
			return;
		}
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

	/**
	 * 在RTEventListener中配置了一个当前实例仅被一个线程调用，所以此处的consume为线程安全的，但是与getReportStatistics可能出现并发操作
	 * @param timeEvent
	 */
	private void consume(TimeEvent timeEvent) {
		long nowSecond = System.currentTimeMillis() / 1000;// 此处以当前时间聚合数据，避免因为eventTime时间延迟导致的allStatistics超出范围以及输出过多日志（之前的统计值输出后又产生了之前的统计值，导致同一秒的统计值输出多次）
		int index = (int) (nowSecond & (STATISTICS_SECOND_SIZE - 1));
		if(allStatistics[index] == null || nowSecond != allStatistics[index].getSecond()) {
			if(allStatistics[index] != null && nowSecond != allStatistics[index].getSecond()) {
				log.warn("未及时输出统计值，已丢弃", allStatistics[index].getSecond());
			}
			allStatistics[index] = new SecondStatisticBean(nowSecond);
		}
		SecondStatisticBean statisticBean = allStatistics[index];
		statisticBean.statistic(timeEvent);
	}

	/**
	 * 获取当前消费时间之前的统计值，这些历史统计值是不变的
	 * @return
	 */
	public List<BaseStatistic> getReportStatistics() {
		List<BaseStatistic> reportStatistic = new ArrayList<>();
		long nowMillis = System.currentTimeMillis();
		for(int i = 0; i < allStatistics.length; i++) {
			SecondStatisticBean statistic = allStatistics[i];
			if(statistic == null || nowMillis / 1000 - statistic.getSecond() <= 1) {// 不存在对应秒数的统计数据或者对应秒的统计还未完成则不输出
				continue;
			}
			for(Entry<String, Map<String, Map<StatisticType, BaseStatistic>>> entryType : statistic.getStatistics().entrySet()) {
				for(Entry<String, Map<StatisticType, BaseStatistic>> entrySign : entryType.getValue().entrySet()) {
					for(Entry<StatisticType, BaseStatistic> entry : entrySign.getValue().entrySet()) {
						reportStatistic.add(entry.getValue());
					}
				}
			}
			allStatistics[i] = null;// 输出后重置
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
