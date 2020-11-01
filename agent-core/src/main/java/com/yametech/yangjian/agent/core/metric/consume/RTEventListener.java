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

import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.bean.MetricData;
import com.yametech.yangjian.agent.api.common.MultiReportFactory;
import com.yametech.yangjian.agent.api.convert.statistic.impl.BaseStatistic;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.common.BaseEventListener;
import com.yametech.yangjian.agent.core.common.EventBusType;
import com.yametech.yangjian.agent.core.metric.base.ConvertTimeEvent;
import com.yametech.yangjian.agent.util.eventbus.consume.BaseConsume;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author liuzhao
 * @Description
 * @date 2019年10月11日 下午4:51:45
 */
public class RTEventListener extends BaseEventListener<ConvertTimeEvent> {
	private static final ILogger log = LoggerFactory.getLogger(RTEventListener.class);
    private static final String CONFIG_KEY = "metricOutput.interval.metric";
    private static final long PERIOD_START_SECOND = LocalDateTime.of(2020, 11, 1, 0, 0,0).toEpochSecond(ZoneOffset.of("+8"));
    private final List<RTEventConsume> consumes = new ArrayList<>();
    private final IReportData report = MultiReportFactory.getReport("statistic");
    private int configInterval = 1;
    private int interval = 1;
    private volatile boolean shutdown = false;

    public RTEventListener() {
		super(EventBusType.METRIC);
	}

    @Override
    public Set<String> configKeyOverride() {
        return new HashSet<>(Collections.singletonList(CONFIG_KEY.replaceAll("\\.", "\\\\.")));
    }

    @Override
    public void configKeyValueOverride(Map<String, String> kv) {
        if (kv == null) {
            return;
        }

        String intervalStr = kv.get(CONFIG_KEY);
        if(intervalStr != null) {
            try {
                configInterval = Integer.parseInt(intervalStr);
            } catch(Exception e) {
                log.warn("{} config error: {}", CONFIG_KEY, intervalStr);
            }
        }
        if(configInterval >= RTEventConsume.STATISTICS_SECOND_SIZE / 2) {
            interval = RTEventConsume.STATISTICS_SECOND_SIZE / 2 - 1;
        } else {
            interval = configInterval;
        }
    }

    @Override
    public BaseConsume<ConvertTimeEvent> getConsume() {
        // 该方法会调用parallelism次，如果返回同一个实例且parallelism>0，则实例为多线程消费
        RTEventConsume consume = new RTEventConsume();
        consumes.add(consume);
        return consume;
    }

    // 注意interval的执行间隔要低于RTEventConsume.STATISTICS_SECOND_SIZE，否则会丢失
    @Override
    public int interval() {
        return interval;
    }
    
    @Override
    public void executeOverride() {
        // 循环consumes输出累加统计值
        for (RTEventConsume consume : consumes) {// 每个consume中的type不重叠
            Collection<BaseStatistic> statistics;
            if(interval > 1) {
                statistics = groupStatistic(consume.getReportStatistics(getMaxSecond()));
            } else {
                statistics = consume.getReportStatistics(null);
            }
            for (BaseStatistic statistic : statistics) {
                Entry<String, Object>[] kvs = statistic.kv();
                if (kvs == null) {
                	continue;
                }
                Map<String, Object> thisParams = new HashMap<>();
                for(Entry<String, Object> entry : kvs) {
                	thisParams.put(entry.getKey(), entry.getValue());
                }
                thisParams.put("period", configInterval);
                MetricData metricData = MetricData.get(statistic.getSecond(), "statistic/" + statistic.getType() + "/" + statistic.statisticType().name(), thisParams);
                if(!report.report(metricData)) {
                	log.warn("report failed: {}", metricData);
                }
            }
        }
    }

    // 仅输出满足完整周期的数据，避免最近一个周期不完整导致输出多出一倍的问题，但是为了避免在关闭时等待过久，在关闭时不限制最大时间，每次输出时将已完成秒内计算的结果输出
    private Long getMaxSecond() {
        return shutdown ? null : (System.currentTimeMillis() / 1000L) - interval;
    }

    /**
     * 将每秒统计值按照输出间隔汇总为周期统计值
     * @param secondStatistic
     * @return
     */
    private Collection<BaseStatistic> groupStatistic(Collection<BaseStatistic> secondStatistic) {
        try {
            Map<String, BaseStatistic> groupStatistic = new HashMap<>();// key组成为：输出周期开始时间/statistic.getType()/statistic.statisticType()/sign
            for (BaseStatistic statistic : secondStatistic) {
                long startSecond = statistic.getSecond() - (statistic.getSecond() - PERIOD_START_SECOND) % configInterval;
                String key = String.join("/",  startSecond + "",
                        statistic.getType(), statistic.statisticType().name(), statistic.getSign());
                BaseStatistic groupValue = groupStatistic.get(key);
                if(groupValue == null) {
                    groupValue = statistic.statisticType().getStatistic();
                    groupValue.reset(statistic.getType(), statistic.getSign(), startSecond);
                    groupStatistic.put(key, groupValue);
                }
                groupValue.combine(statistic);
            }
            return groupStatistic.values();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean shutdown(Duration duration) {
        shutdown = true;
        return super.shutdown(duration);
    }

    @Override
    public long waitMillis() {
        return interval * 1000L + 100L;// 必须大于interval，否则可能导致内存中数还未输出完就停止了
    }

    @Override
    protected long getTotalNum() {
        long totalNum = 0;
        for (RTEventConsume consume : consumes) {
            totalNum += consume.getTotalNum();
        }
        return totalNum;
    }

    @Override
    protected long getPeriodNum() {
    	long periodNum = 0;
        for (RTEventConsume consume : consumes) {
            periodNum += consume.getPeriodTotalNum();
        }
        return periodNum;
    }
    
	@Override
	protected int eventHashCode(ConvertTimeEvent event) {
		return event.getType().hashCode();
	}
}
