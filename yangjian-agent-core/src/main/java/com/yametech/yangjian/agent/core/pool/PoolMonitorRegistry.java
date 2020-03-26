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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.api.pool.IPoolMonitor;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public enum PoolMonitorRegistry {
    INSTANCE;

    private static final ILogger LOGGER = LoggerFactory.getLogger(PoolMonitorRegistry.class);
    private static final int POOL_INSTANCE_MAX = 50;
    private final List<IPoolMonitor> poolMonitors = new CopyOnWriteArrayList<>();

	boolean register(IPoolMonitor poolMonitor) {
    	if(poolMonitor == null) {
    		return false;
    	}
    	if(poolMonitors.size() > POOL_INSTANCE_MAX) {
    		LOGGER.warn("Init too many pool instance：{}", POOL_INSTANCE_MAX);
    		return false;
    	}
    	synchronized (poolMonitors) {
    		if(poolMonitors.contains(poolMonitor)) {
    			return true;
    		}
    		poolMonitors.add(poolMonitor);
    		LOGGER.info("PoolMonitor[{}/{}] register.", poolMonitor.getType(), poolMonitor.getIdentify());
		}
        return true;
    }

    boolean unregister(IPoolMonitor poolMonitor) {
    	if(poolMonitor == null) {
    		return false;
    	}
    	synchronized (poolMonitors) {
    		poolMonitors.remove(poolMonitor);
    		LOGGER.info("PoolMonitor[{}/{}] unregister.", poolMonitor.getType(), poolMonitor.getIdentify());
    		return true;
    	}
    }

    List<IPoolMonitor> getPoolMonitors() {
        return poolMonitors;
    }
}
