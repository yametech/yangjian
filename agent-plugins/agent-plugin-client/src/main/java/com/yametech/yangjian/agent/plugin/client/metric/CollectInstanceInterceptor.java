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
package com.yametech.yangjian.agent.plugin.client.metric;

import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.bean.MetricData;
import com.yametech.yangjian.agent.api.common.MultiReportFactory;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.client.IStatusCollect;
import com.yametech.yangjian.agent.client.StatusReturn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * 
 * @Description 
 * 
 * @author liuzhao
 * @date 2020年5月6日 下午5:13:31
 */
public class CollectInstanceInterceptor implements IConstructorListener, ISchedule {
	private static final ILogger LOG = LoggerFactory.getLogger(CollectInstanceInterceptor.class);
	private static final int MAX_INSTANCE_NUMBER = 500;
	private static final int TYPE_MAX_LENGTH = 20;
    private static final int REASON_MAX_LENGTH = 200;
    private IReportData report = MultiReportFactory.getReport("collect");
	private Map<IStatusCollect, Long> collects = new ConcurrentHashMap<>();
	
	@Override
	public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
		register((IStatusCollect) thisObj);
	}
	
	private synchronized boolean register(IStatusCollect collect) {
    	if(collect == null) {
    		return false;
    	}
    	if(collects.size() > MAX_INSTANCE_NUMBER) {
    		LOG.warn("Init too many instance：{}", MAX_INSTANCE_NUMBER);
    		return false;
    	}
		if(collects.containsKey(collect)) {
			return true;
		}
		collects.put(collect, System.currentTimeMillis());
		LOG.info("register {} {} {}", collect.getClass(), collect.type(), collect.interval());
        return true;
    }

	@Override
    public int interval() {
        return 1;
    }

    @Override
    public void execute() {
    	Stream<Entry<IStatusCollect, Long>> stream;
		if(collects.size() > 3) {
			stream = collects.entrySet().parallelStream();
		} else {
			stream = collects.entrySet().stream();
		}
		List<IStatusCollect> inactives = new ArrayList<>();
		long now = System.currentTimeMillis();
		stream.forEach(entry -> {
			if(now - entry.getValue() < entry.getKey().interval() * 1000) {
				return;
			}
			if(entry.getKey().status() == -1) {
        		inactives.add(entry.getKey());
        		return;
        	}
        	if(entry.getKey().status() == 0) {
        		return;
        	}
        	String type = entry.getKey().type();
        	if(type != null && type.length() > TYPE_MAX_LENGTH) {
        		type = type.substring(0, TYPE_MAX_LENGTH);
        	}
        	StatusReturn statusReturn = entry.getKey().collect();
        	if(statusReturn == null) {
				entry.setValue(now);
				return;
			}
        	String reason = statusReturn.getReason();
        	if(reason != null && reason.length() > REASON_MAX_LENGTH) {
        		reason = reason.substring(0, REASON_MAX_LENGTH);
        	}
        	Map<String, Object> params = new HashMap<>();
        	params.put("sign", entry.getKey().getClass().getName());
        	params.put("reason", reason);
        	MetricData metricData = MetricData.get(null, "collect/" + type + "/" + statusReturn.getLevel().name(), params);
        	if(!report.report(metricData)) {
            	LOG.warn("上报失败： {}", metricData);
            }
            entry.setValue(now);
		});
		inactives.forEach(collects::remove);
    }
	
}
