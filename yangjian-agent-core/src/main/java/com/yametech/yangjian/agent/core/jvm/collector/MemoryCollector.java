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

package com.yametech.yangjian.agent.core.jvm.collector;

import com.yametech.yangjian.agent.core.jvm.metrics.MemoryMetrics;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * @author zcn
 * @date: 2019-10-17
 **/
public final class MemoryCollector implements IMetricsCollector {

    private static final ILogger logger = LoggerFactory.getLogger(MemoryCollector.class);
    private static final String DIRECT_BUFFER_MBEAN = "java.nio:type=BufferPool,name=direct";
    private static final String MAPPED_BUFFER_MBEAN = "java.nio:type=BufferPool,name=mapped";

    private MemoryMXBean memoryMXBean;
    private MBeanServer mbeanServer;

    public MemoryCollector() {
        memoryMXBean = ManagementFactory.getMemoryMXBean();
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    @Override
    public MemoryMetrics collect() {
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        return new MemoryMetrics(
                collectMappedCache() >> 10,
                collectDirectMemory() >> 10,
                heapMemoryUsage.getUsed() >> 10,
                nonHeapMemoryUsage.getUsed() >> 10);
    }

    public long collectDirectMemory() {
        try {
            ObjectName directPool = new ObjectName(DIRECT_BUFFER_MBEAN);
            return (Long) mbeanServer.getAttribute(directPool, "MemoryUsed");
        } catch (Exception e) {
            logger.error(e, "fail to collect direct memory");
        }
        return 0L;
    }

    public long collectMappedCache() {
        try {
            ObjectName directPool = new ObjectName(MAPPED_BUFFER_MBEAN);
            return (Long) mbeanServer.getAttribute(directPool, "MemoryUsed");
        } catch (Exception e) {
            logger.error(e, "fail to collect mapped cache");
        }
        return 0L;
    }

}
