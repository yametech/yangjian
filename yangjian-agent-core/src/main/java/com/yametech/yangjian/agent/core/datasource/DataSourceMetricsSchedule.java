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

package com.yametech.yangjian.agent.core.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yametech.yangjian.agent.api.IDataSourceMonitor;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.core.core.InstanceManage;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.report.ReportManage;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public class DataSourceMetricsSchedule implements ISchedule {
    private static final ILogger LOGGER = LoggerFactory.getLogger(DataSourceMetricsSchedule.class);
    private IReportData report = InstanceManage.loadInstance(ReportManage.class, 
    		new Class[] {Class.class}, new Object[] {this.getClass()});
    
    @Override
    public int interval() {
        return 1;
    }

    @Override
    public void execute() {
        try {
            List<IDataSourceMonitor> dataSourceMonitors = DataSourceMonitorRegistry.INSTANCE.getDataSourceMonitors();
            for (IDataSourceMonitor monitor : dataSourceMonitors) {
            	Map<String, Object> params = new HashMap<>();
            	params.put(monitor.getType() + "_active_count", monitor.getActiveCount());
            	params.put(monitor.getType() + "_max_total", monitor.getMaxTotalConnectionCount());
            	report.report("statistic/" + monitor.getType() + "/connectionPool", null, params);
            }
        } catch (Exception e) {
            LOGGER.error(e, "DataSourceMetricsScheduler execute error.");
        }
    }
}
