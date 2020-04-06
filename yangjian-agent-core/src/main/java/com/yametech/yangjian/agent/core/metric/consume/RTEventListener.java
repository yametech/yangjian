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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.convert.statistic.impl.BaseStatistic;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.common.BaseEventListener;
import com.yametech.yangjian.agent.core.metric.MetricData;
import com.yametech.yangjian.agent.core.metric.base.ConvertTimeEvent;
import com.yametech.yangjian.agent.core.report.ReportManage;
import com.yametech.yangjian.agent.util.eventbus.consume.BaseConsume;

/**
 * @author liuzhao
 * @Description
 * @date 2019年10月11日 下午4:51:45
 */
public class RTEventListener extends BaseEventListener<ConvertTimeEvent> {
	private static ILogger log = LoggerFactory.getLogger(RTEventListener.class);
    private List<RTEventConsume> consumes = new ArrayList<>();
    private IReportData report = ReportManage.getReport("RTEventListener");
    
    public RTEventListener() {
		super(Constants.ProductConsume.METRIC, "metric.consume.threadNum", ReportManage.getReport("RTEventListener"));
	}

    @Override
    public BaseConsume<ConvertTimeEvent> getConsume() {
        // 该方法会调用parallelism次，如果返回同一个实例且parallelism>0，则实例为多线程消费
        RTEventConsume consume = new RTEventConsume();
        consumes.add(consume);
        return consume;
    }

    @Override
    public void execute() {
    	super.execute();
        // 循环consumes输出累加统计值，或者直接输出每个的统计值，ecpark-monitor做聚合
        for (RTEventConsume consume : consumes) {
            for (BaseStatistic statistic : consume.getReportStatistics()) {
                Entry<String, Object>[] kvs = statistic.kv();
                if (kvs == null) {
                	continue;
                }
                Map<String, Object> thisParams = new HashMap<>();
                for(Entry<String, Object> entry : kvs) {
                	thisParams.put(entry.getKey(), entry.getValue());
                }
                MetricData metricData = MetricData.get(statistic.getSecond(), "statistic/" + statistic.getType() + "/" + statistic.statisticType(), thisParams);
                if(!report.report(metricData)) {
                	log.warn("上报异常： {}", metricData);
                }
            }
        }
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
