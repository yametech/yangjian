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

package com.yametech.yangjian.agent.core.metric.consume;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.convert.statistic.impl.BaseStatistic;
import com.yametech.yangjian.agent.core.core.InstanceManage;
import com.yametech.yangjian.agent.core.metric.base.ConvertTimeEvent;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.report.ReportManage;
import com.yametech.yangjian.agent.util.eventbus.consume.BaseConsume;
import com.yametech.yangjian.agent.util.eventbus.consume.ConsumeFactory;

/**
 * @author liuzhao
 * @Description
 * @date 2019年10月11日 下午4:51:45
 */
public class RTEventListener implements IAppStatusListener, ConsumeFactory<ConvertTimeEvent>, ISchedule {
    private static final ILogger log = LoggerFactory.getLogger(RTEventListener.class);
    private List<RTEventConsume> consumes = new ArrayList<>();
    private IReportData report = InstanceManage.loadInstance(ReportManage.class, 
    		new Class[] {Class.class}, new Object[] {this.getClass()});
    
//    private Map<String, String> configMatches = new HashMap<>();
//    private List<IConfigMatch> matches = new ArrayList<>();

//    @Override
//    public Set<String> configKey() {
//        return new HashSet<>(Arrays.asList("RTEventListener", "RTEventListener\\..*"));
//    }
//
//    @Override
//    public void configKeyValue(Map<String, String> kv) {
//        if (kv == null) {
//            return;
//        }
//        this.configMatches = kv;
////		kv.entrySet().stream()
//////			.filter(value -> value != null && !value.trim().isEmpty())
////			.forEach(value -> configMatches.put(value.getKey(), new Set)(Arrays.asList(value.split("\r\n"))));
//    }

    @Override
    public void beforeRun() {
//        Set<String> configMatchesValues = new HashSet<>();
//        configMatches.entrySet().forEach(entry -> configMatchesValues.addAll(Arrays.asList(entry.getValue().split("\r\n"))));
//        configMatchesValues.forEach(match -> matches.add(new MethodRegexMatch(match)));
    }

//    @Override
//    public IConfigMatch match() {
//        return new CombineOrMatch(matches);
//    }

    @Override
    public BaseConsume<ConvertTimeEvent> getConsume() {
        // 一个实例为单线程消费，如果此处返回同一个实例且配置了多线程消费，则实例为多线程消费
        RTEventConsume consume = new RTEventConsume();
        consumes.add(consume);
        return consume;
    }

    @Override
    public int interval() {
        return 2;
    }

    @Override
    public void execute() {
    	Map<String, Object> params = new HashMap<>();
    	params.put("total_num", getTotalNum());
    	params.put("period_seconds", interval());
    	params.put("period_num", getPeriodNum());
    	report.report("method-event/consume", null, params);
        // 循环consumes输出累加统计值，或者直接输出每个的统计值，ecpark-monitor做聚合
        for (RTEventConsume consume : consumes) {
            for (BaseStatistic statistic : consume.getReportStatistics()) {
                Entry<String, Object>[] kvs = statistic.kv();
                if (kvs != null) {
                	Map<String, Object> thisParams = new HashMap<>();
                	for(Entry<String, Object> entry : kvs) {
                		thisParams.put(entry.getKey(), entry.getValue());
                	}
                	report.report("statistic/" + statistic.getType() + "/" + statistic.statisticType(), statistic.getSecond(), thisParams);
                }
            }
        }
    }

    private int getTotalNum() {
        int totalNum = 0;
        for (RTEventConsume consume : consumes) {
            totalNum += consume.getTotalNum();
        }
        return totalNum;
    }

    private int getPeriodNum() {
        int periodNum = 0;
        for (RTEventConsume consume : consumes) {
            periodNum += consume.getPeriodTotalNum();
        }
        return periodNum;
    }

    @Override
    public boolean shutdown(Duration duration) {
        long previousNum = 0;
        while (previousNum < getTotalNum()) {// N毫秒内无调用事件则关闭，避免因关闭服务导致事件丢失
            try {
                previousNum = getTotalNum();
                Thread.sleep(interval() * 1002L);
            } catch (InterruptedException e) {
                log.warn(e, "shutdown interrupted");
                Thread.currentThread().interrupt();
                break;
            }
        }
        return true;
    }

//	@Override
//	public int parallelism() {
//		return 3;
//	}
}
