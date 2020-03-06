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

package com.yametech.yangjian.agent.plugin.hikaricp.monitor;

import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.api.pool.IPoolMonitor;

import java.lang.reflect.Method;

/**
 * @author dengliming
 * @date 2019/12/22
 */
public class HikariDataSourceMonitor implements IPoolMonitor {

    private static final ILogger LOGGER = LoggerFactory.getLogger(HikariDataSourceMonitor.class);
    private final String jdbcUrl;
    private final Object hikariPool;
    private final Method getActiveConnectionsMethod;
    private final Method getTotalConnectionsMethod;
    private boolean isActive = true;

    public HikariDataSourceMonitor(Object hikariPool, String jdbcUrl) {
        this.hikariPool = hikariPool;
        this.jdbcUrl = jdbcUrl;
        try {
            this.getActiveConnectionsMethod = getActiveConnectionsMethod(hikariPool);
            this.getTotalConnectionsMethod = getTotalConnectionsMethod(hikariPool);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public String getType() {
        return Constants.EventType.HIKARICP;
    }

    @Override
    public int getActiveCount() {
        try {
            Object result = getActiveConnectionsMethod.invoke(hikariPool);
            return (Integer) result;
        } catch (Exception e) {
            LOGGER.error(e, "Execute getActiveCount error.");
        }
        return 0;
    }

    @Override
    public int getMaxTotalConnectionCount() {
        try {
            Object result = getTotalConnectionsMethod.invoke(hikariPool);
            return (Integer) result;
        } catch (Exception e) {
            LOGGER.error(e, "Execute getMaxTotalConnectionCount error.");
        }
        return 0;
    }

    private Method getActiveConnectionsMethod(Object object) throws NoSuchMethodException {
        Method getActiveConnectionsMethod = object.getClass().getMethod("getActiveConnections");
        if (getActiveConnectionsMethod == null) {
            throw new IllegalArgumentException("object must has getActiveConnections method");
        }

        Class<?> returnType = getActiveConnectionsMethod.getReturnType();
        if (int.class != returnType) {
            throw new IllegalArgumentException("invalid return type. expected:int, actual:" + returnType);
        }

        return getActiveConnectionsMethod;
    }

    private Method getTotalConnectionsMethod(Object object) throws NoSuchMethodException {
        Method getTotalConnections = object.getClass().getMethod("getTotalConnections");
        if (getTotalConnections == null) {
            throw new IllegalArgumentException("object must has getTotalConnections method");
        }

        Class<?> returnType = getTotalConnections.getReturnType();
        if (int.class != returnType) {
            throw new IllegalArgumentException("invalid return type. expected:int, actual:" + returnType);
        }

        return getTotalConnections;
    }

    @Override
    public String getIdentify() {
        return jdbcUrl;
    }
    
    
    @Override
    public boolean isActive() {
    	return isActive;
    }
    
    public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
    
}
