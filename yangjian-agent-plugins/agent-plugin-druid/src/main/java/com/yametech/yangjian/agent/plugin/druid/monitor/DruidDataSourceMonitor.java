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

package com.yametech.yangjian.agent.plugin.druid.monitor;

import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.pool.IPoolMonitor;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public class DruidDataSourceMonitor implements IPoolMonitor {

    private final DruidDataSource dataSource;
    private boolean isActive = true;

    public DruidDataSourceMonitor(DruidDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getType() {
        return Constants.EventType.DRUID;
    }

    @Override
    public int getActiveCount() {
        return dataSource == null ? 0 : dataSource.getActiveCount();
    }

    @Override
    public int getMaxTotalConnectionCount() {
        return dataSource == null ? 0 : dataSource.getMaxActive();
    }

    @Override
    public String getIdentify() {
    	return dataSource == null ? "Unknown" : dataSource.getUrl();
    }
    
    @Override
    public boolean isActive() {
    	return isActive;
    }
    
    public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
    
}
