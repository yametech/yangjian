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

import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.bean.MetricData;
import com.yametech.yangjian.agent.api.common.MultiReportFactory;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.util.eventbus.assignor.MultiThreadAssignor;
import com.yametech.yangjian.agent.util.eventbus.consume.ConsumeFactory;

import java.time.Duration;
import java.util.*;

/**
 * @author liuzhao
 * @Description
 * @date 2019年10月11日 下午4:51:45
 */
public abstract class BaseEventListener<T> implements IAppStatusListener, ConsumeFactory<T>, ISchedule, IConfigReader {
    private static final ILogger log = LoggerFactory.getLogger(BaseEventListener.class);
    private static final String THREADNUM_KEY_PREFIX = "consume.threadNum.";
    private static final String INTERVAL_KEY_PREFIX = "metricOutput.interval.consume.";
    private int threadNum = 1;
    private String configKeySuffix;
    private IReportData report = MultiReportFactory.getReport("eventListener");
    private String metricType;
    private int interval = 10;
    
    public BaseEventListener(EventBusType configKeySuffix) {
    	this.metricType = configKeySuffix.getMetricType();
    	this.configKeySuffix = configKeySuffix.getConfigKeySuffix();
	}
    
    @Override
    public Set<String> configKey() {
        return new HashSet<>(Arrays.asList(THREADNUM_KEY_PREFIX.replaceAll("\\.", "\\\\.") + configKeySuffix, 
        		INTERVAL_KEY_PREFIX.replaceAll("\\.", "\\\\.") + configKeySuffix));
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
        		log.warn("{} config error: {}", THREADNUM_KEY_PREFIX + configKeySuffix, threadNumStr);
        	}
        }
        
        String intervalStr = kv.get(INTERVAL_KEY_PREFIX + configKeySuffix);
    	if(intervalStr != null) {
    		try {
    			interval = Integer.parseInt(intervalStr);
            } catch(Exception e) {
            	log.warn("{} config error: {}", INTERVAL_KEY_PREFIX + configKeySuffix, intervalStr);
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
    	params.put("period_seconds", interval);
    	params.put("period_num", getPeriodNum());
    	MetricData metricData = MetricData.get(null, "consume/" + metricType, params);
    	if(!report.report(metricData)) {
    		log.warn("report failed : {}", metricData);
    	}
    }

    @Override
    public boolean shutdown(Duration duration) {
        long previousNum = 0;
        while (previousNum < getTotalNum()) {// N毫秒内无调用事件则关闭，避免因关闭服务导致事件丢失
            try {
                previousNum = getTotalNum();
                //noinspection BusyWait
                Thread.sleep(502L);
            } catch (InterruptedException e) {
                log.warn(e, "shutdown interrupted");
                Thread.currentThread().interrupt();
                return false;
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
			return (msg, totalThreadNum) -> Math.abs(eventHashCode(msg) % totalThreadNum);
		} else {
			return null;
		}
	}
	
	@Override
    public int weight() {
    	return IAppStatusListener.super.weight() + 20;// 要高于BaseEventPublish中的权重
    }
	
    protected abstract long getTotalNum();
    protected abstract long getPeriodNum();
    
    /**
     * 	是否启用hash分片，用于针对一种类型数据需要保证顺序消费的场景
     * threadNum大于1时有用
     * @return
     */
    protected boolean hashShard() {
    	return false;
    }
    
    /**
     * 	业务hash值，用于将同类型业务绑定在一个线程消费
     * threadNum大于1且hashShard为true时有用
     * @param event
     * @return
     */
	protected int eventHashCode(T event) {
		return event.hashCode();
	}
}
