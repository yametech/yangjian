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

package com.yametech.yangjian.agent.plugin.hikaricp.context;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import com.yametech.yangjian.agent.core.datasource.DataSourceMonitorRegistry;
import com.yametech.yangjian.agent.plugin.hikaricp.monitor.HikariDataSourceMonitor;
import com.zaxxer.hikari.HikariConfig;

/**
 * 拦截连接池构造方法
 *
 * @author dengliming
 * @date 2019/12/21
 */
public class HikariPoolConstructorInterceptor implements IConstructorListener {

    private final DataSourceMonitorRegistry dataSourceMonitorRegistry = DataSourceMonitorRegistry.INSTANCE;

    @Override
    public void constructor(Object thisObj, Object[] allArguments) {
        if (!(allArguments[0] instanceof HikariConfig)) {
            return;
        }
        HikariConfig hikariConfig = (HikariConfig) allArguments[0];
        String jdbcUrl = hikariConfig.getJdbcUrl();
        if (StringUtil.notEmpty(jdbcUrl)) {
            // 为了兼容不同版本该方法有可能被多次执行，所以先判断下当前类上下文是否已经存在该连接池避免重复设置
            HikariDataSourceMonitor hikariDataSourceMonitor = (HikariDataSourceMonitor) ((IContext) thisObj)._getAgentContext(ContextConstants.DATA_SOURCE_CONTEXT_FIELD);
            if (hikariDataSourceMonitor == null) {
                hikariDataSourceMonitor = new HikariDataSourceMonitor(thisObj, jdbcUrl);
                ((IContext) thisObj)._setAgentContext(ContextConstants.DATA_SOURCE_CONTEXT_FIELD, hikariDataSourceMonitor);
                dataSourceMonitorRegistry.register(hikariDataSourceMonitor);
            }
        }
    }
}
