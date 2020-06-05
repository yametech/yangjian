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
package com.yametech.yangjian.agent.core.pool;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.bean.MetricData;
import com.yametech.yangjian.agent.api.common.MultiReportFactory;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.api.pool.IPoolMonitor;

import java.util.*;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public class PoolMonitorSchedule implements ISchedule, IConfigReader {
    private static final ILogger LOG = LoggerFactory.getLogger(PoolMonitorSchedule.class);
    private static final String CONFIG_KEY = "metricOutput.interval.pool";
    private IReportData report = MultiReportFactory.getReport("poolMonitor");
    private int interval = 5;
    
    @Override
    public Set<String> configKey() {
        return new HashSet<>(Collections.singletonList(CONFIG_KEY.replaceAll("\\.", "\\\\.")));
    }

    @Override
    public void configKeyValue(Map<String, String> kv) {
        if (kv == null) {
            return;
        }
        
        String intervalStr = kv.get(CONFIG_KEY);
    	if(intervalStr != null) {
    		try {
    			interval = Integer.parseInt(intervalStr);
            } catch(Exception e) {
            	LOG.warn("{} config error: {}", CONFIG_KEY, intervalStr);
            }
    	}
    }
    
    @Override
    public int interval() {
        return interval;
    }

    @Override
    public void execute() {
        try {
            List<IPoolMonitor> poolMonitors = PoolMonitorRegistry.INSTANCE.getPoolMonitors();
            List<IPoolMonitor> inactives = new ArrayList<>();
            for (IPoolMonitor monitor : poolMonitors) {
            	if(!monitor.isActive()) {
            		inactives.add(monitor);
            		continue;
            	}
            	Map<String, Object> params = new HashMap<>();
            	params.put("active_count", monitor.getActiveCount());
            	params.put("max_total", monitor.getMaxTotalConnectionCount());
            	params.put("sign", monitor.getIdentify());
            	report.report(MetricData.get(null, "statistic/" + monitor.getType() + "/connectionPool", params));
            }
            inactives.forEach(PoolMonitorRegistry.INSTANCE::unregister);
        } catch (Exception e) {
            LOG.error(e, "execute error.");
        }
    }
}
