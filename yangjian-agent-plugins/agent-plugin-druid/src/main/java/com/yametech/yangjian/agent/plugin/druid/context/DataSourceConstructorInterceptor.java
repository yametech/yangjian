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

package com.yametech.yangjian.agent.plugin.druid.context;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import com.yametech.yangjian.agent.core.datasource.DataSourceMonitorRegistry;
import com.yametech.yangjian.agent.plugin.druid.monitor.DruidDataSourceMonitor;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public class DataSourceConstructorInterceptor implements IConstructorListener {

    private final DataSourceMonitorRegistry dataSourceMonitorRegistry = DataSourceMonitorRegistry.INSTANCE;

    @Override
    public void constructor(Object thisObj, Object[] allArguments) {
        DruidDataSourceMonitor druidDataSourceMonitor = new DruidDataSourceMonitor((DruidDataSource) thisObj);
        ((IContext) thisObj)._setAgentContext(ContextConstants.DATA_SOURCE_CONTEXT_FIELD, druidDataSourceMonitor);
        dataSourceMonitorRegistry.register(druidDataSourceMonitor);
    }
}
