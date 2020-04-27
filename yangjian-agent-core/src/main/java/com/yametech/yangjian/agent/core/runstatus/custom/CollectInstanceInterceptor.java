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

package com.yametech.yangjian.agent.core.runstatus.custom;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.client.IStatusCollect;
import com.yametech.yangjian.agent.client.StatusReturn;
import com.yametech.yangjian.agent.core.metric.MetricData;
import com.yametech.yangjian.agent.core.report.MultiReportFactory;

public class CollectInstanceInterceptor implements IConstructorListener, ISchedule {
	private static final ILogger LOG = LoggerFactory.getLogger(CollectInstanceInterceptor.class);
	private static IReportData report = MultiReportFactory.getReport("collectStatus");
	private static Map<IStatusCollect, Long> collects = new ConcurrentHashMap<>();
	
	@Override
	public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
		collects.put((IStatusCollect) thisObj, System.currentTimeMillis());
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
		long now = System.currentTimeMillis();
		stream.forEach(entry -> {
			if(now - entry.getValue() < entry.getKey().interval() * 1000) {
				return;
			}
			StatusReturn ret = entry.getKey().collect();
			Map<String, Object> map = new HashMap<>();
			map.put("level", ret.getLevel().name());
			map.put("reason", ret.getReason());
			MetricData metricData = MetricData.get("customMetric/" + entry.getKey().type(), map);
            if(!report.report(metricData)) {
            	LOG.warn("上报失败： {}", metricData);
            }
            entry.setValue(now);
		});
	}

}
