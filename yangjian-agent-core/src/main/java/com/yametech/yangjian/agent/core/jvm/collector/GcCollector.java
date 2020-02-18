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

import com.yametech.yangjian.agent.core.jvm.metrics.JVMGcMetrics;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 垃圾回收指标数据采集
 *
 * @author dengliming
 * @date 2019/12/27
 */
public final class GcCollector implements IMetricsCollector {

    private static final Set<String> YOUNG_GC_SET = new HashSet<>(Arrays.asList("Copy", "ParNew", "PS Scavenge", "G1 Young Generation"));

    private static final Set<String> OLD_GC_SET = new HashSet<>(Arrays.asList("MarkSweepCompact", "PS MarkSweep", "ConcurrentMarkSweep", "G1 Old Generation"));

    private long lastYoungGcTime = 0L;

    private long lastYoungGcCount = 0L;

    private long lastOldGcTime = 0L;

    private long lastOldGcCount = 0L;

    private List<GarbageCollectorMXBean> garbageCollectorMXBeans;

    public GcCollector() {
        garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
    }

    @Override
    public JVMGcMetrics collect() {
        long youngGcCount = 0L;
        long youngGcTime = 0L;
        long oldGcCount = 0L;
        long oldGcTime = 0L;
        for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
            String gcName = garbageCollectorMXBean.getName();
            if (YOUNG_GC_SET.contains(gcName)) {
                youngGcTime += garbageCollectorMXBean.getCollectionTime();
                youngGcCount += garbageCollectorMXBean.getCollectionCount();
            } else if (OLD_GC_SET.contains(gcName)) {
                oldGcTime += garbageCollectorMXBean.getCollectionTime();
                oldGcCount += garbageCollectorMXBean.getCollectionCount();
            }
        }

        JVMGcMetrics jvmGcMetrics = new JVMGcMetrics(
                youngGcCount - lastYoungGcCount,
                youngGcTime - lastYoungGcTime,
                oldGcCount - lastOldGcCount,
                oldGcTime - lastOldGcTime);
        lastYoungGcCount = youngGcCount;
        lastYoungGcTime = youngGcTime;
        lastOldGcCount = oldGcCount;
        lastOldGcTime = oldGcTime;
        return jvmGcMetrics;
    }
}
