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
package com.yametech.yangjian.agent.api.pool;

/**
 * @author dengliming
 */
public interface IPoolMonitor {

	/**
	 * @return pool type,for example connection pool:druid,hikaricp.thread pool:dubbo
	 */
    String getType();

    /**
     * 活跃连接数
     *
     * @return	int
     */
    int getActiveCount();

    /**
     * 最大连接数
     *
     * @return	int
     */
    int getMaxTotalConnectionCount();

    /**
     * @return	pool status
     * 	true：monitor pool
     * 	false：remove this pool instance from monitor
     */
    default boolean isActive() {
    	return true;
    }
    
    default String getIdentify() {
        return "Unknown";
    }
}
