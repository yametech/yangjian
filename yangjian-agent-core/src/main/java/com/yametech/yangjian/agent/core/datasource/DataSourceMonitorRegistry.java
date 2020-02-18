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

import com.yametech.yangjian.agent.api.IDataSourceMonitor;
import com.yametech.yangjian.agent.core.log.ILogger;
import com.yametech.yangjian.agent.core.log.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public enum DataSourceMonitorRegistry {
    INSTANCE;

    private static final ILogger LOGGER = LoggerFactory.getLogger(DataSourceMonitorRegistry.class);
    private final List<IDataSourceMonitor> dataSourceMonitors = new ArrayList<>();

    public boolean register(IDataSourceMonitor dataSourceMonitor) {
        dataSourceMonitors.add(dataSourceMonitor);
        LOGGER.info("DataSourceMonitor[{}/{}] register.", dataSourceMonitor.getType(), dataSourceMonitor.getJdbcUrl());
        return true;
    }

    public boolean unregister(IDataSourceMonitor dataSourceMonitor) {
        dataSourceMonitors.remove(dataSourceMonitor);
        LOGGER.info("DataSourceMonitor[{}/{}] unregister.", dataSourceMonitor.getType(), dataSourceMonitor.getJdbcUrl());
        return true;
    }

    public List<IDataSourceMonitor> getDataSourceMonitors() {
        return dataSourceMonitors;
    }
}
