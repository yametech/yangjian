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
