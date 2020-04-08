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
package com.yametech.yangjian.agent.core.common;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.metric.MetricData;
import com.yametech.yangjian.agent.util.eventbus.assignor.MultiThreadAssignor;
import com.yametech.yangjian.agent.util.eventbus.consume.ConsumeFactory;

/**
 * @author liuzhao
 * @Description
 * @date 2019年10月11日 下午4:51:45
 */
public abstract class BaseEventListener<T> implements IAppStatusListener, ConsumeFactory<T>, ISchedule, IConfigReader {
    private static final ILogger log = LoggerFactory.getLogger(BaseEventListener.class);
    private static final String THREADNUM_KEY_PREFIX = "consume.threadNum.";
    private static final String INTERVAL_KEY_PREFIX = "consumeMetricOutput.interval.";
    private int threadNum = 1;
    private String configKeySuffix;
    private IReportData report;
    private String type;
    private int interval = 5;
    
    public BaseEventListener(String type, String configKeySuffix, IReportData report) {
		this.configKeySuffix = configKeySuffix;
		this.report = report;
		this.type = type;
	}
    
    @Override
    public Set<String> configKey() {
        return new HashSet<>(Arrays.asList(THREADNUM_KEY_PREFIX + configKeySuffix, INTERVAL_KEY_PREFIX + configKeySuffix));
    }

    @Override
    public void configKeyValue(Map<String, String> kv) {
        if (kv == null) {
            return;
        }
        String threadNumStr = kv.get(THREADNUM_KEY_PREFIX + configKeySuffix);
        if(threadNumStr != null) {
        	try {
        		int num = Integer.parseInt(threadNumStr.trim());
        		if(num > 0) {
        			threadNum = num;
        		}
        	} catch(Exception e) {
        		log.warn("{}配置错误：{}", THREADNUM_KEY_PREFIX + configKeySuffix, threadNumStr);
        	}
        }
        
        String intervalStr = kv.get(INTERVAL_KEY_PREFIX + configKeySuffix);
    	if(intervalStr != null) {
    		try {
    			interval = Integer.parseInt(intervalStr);
            } catch(Exception e) {
            	log.warn("{}配置错误：{}", INTERVAL_KEY_PREFIX + configKeySuffix, intervalStr);
            }
    	}
    }
    
    @Override
    public void beforeRun() {
    	// 无需处理
    }

    @Override
    public int interval() {
        return interval;
    }

    @Override
    public void execute() {
    	Map<String, Object> params = new HashMap<>();
    	params.put("total_num", getTotalNum());
    	params.put("period_seconds", interval());
    	params.put("period_num", getPeriodNum());
    	MetricData metricData = MetricData.get(null, "consume/" + type, params);
    	if(!report.report(metricData)) {
    		log.warn("上报失败：{}", metricData);
    	}
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

	@Override
	public int parallelism() {
		return threadNum;
	}
	
	@Override
	public MultiThreadAssignor<T> assignor() {
		if(threadNum > 1) {
			return (msg, totalThreadNum) -> eventHashCode(msg) % totalThreadNum;
		} else {
			return null;
		}
	}
	
    protected abstract long getTotalNum();
    protected abstract long getPeriodNum();
	protected abstract int eventHashCode(T event);
}
