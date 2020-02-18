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

package com.yametech.yangjian.agent.core.jvm;

import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.core.jvm.collector.*;
import com.yametech.yangjian.agent.core.jvm.metrics.*;
import com.yametech.yangjian.agent.core.jvm.collector.*;
import com.yametech.yangjian.agent.core.jvm.metrics.*;
import com.yametech.yangjian.agent.core.log.ILogger;
import com.yametech.yangjian.agent.core.log.LoggerFactory;
import com.yametech.yangjian.agent.core.util.LogUtil;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author zcn
 * @date: 2019-10-25
 **/
public class JVMMetricsSchedule implements IAppStatusListener, ISchedule {

    private static final ILogger logger = LoggerFactory.getLogger(JVMMetricsSchedule.class);

    private BufferPoolCollector bufferPoolCollector;
    private GcCollector gcCollector;
    private ThreadCollector threadCollector;
    private MemoryCollector memoryCollector;
    private MemoryPoolCollector memoryPoolCollector;
    private ProcessCollector processCollector;
    private ClassCollector classCollector;

    @Override
    public void beforeRun() {
        bufferPoolCollector = new BufferPoolCollector();
        gcCollector = new GcCollector();
        threadCollector = new ThreadCollector();
        memoryCollector = new MemoryCollector();
        memoryPoolCollector = new MemoryPoolCollector();
        processCollector = new ProcessCollector();
        classCollector = new ClassCollector();
    }

    @Override
    public int interval() {
        return 1;
    }

    @Override
    public void execute() {
        try {
            processMemoryMetrics();
            processBufferPoolMetrics();
            processJVMMetrics();
            processThreadMetrics();
        } catch (Exception e) {
            logger.error(e, "collect jvm metrics error");
        }
    }

    @Override
    public boolean shutdown(Duration duration) {
        return true;
    }

    private void processMemoryMetrics() {
        List<Entry<String, Object>> simpleEntryList = new ArrayList<>();
        MemoryMetrics memoryMetrics = memoryCollector.collect();
        ProcessMetrics processMetrics = processCollector.collect();
        List<MemoryPoolMetrics> memoryPoolMetricsList = memoryPoolCollector.collect();
        for (MemoryPoolMetrics memoryPoolMetrics : memoryPoolMetricsList) {
            simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>(memoryPoolMetrics.getType().name().toLowerCase(), memoryPoolMetrics.getUsed()));
        }
        simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>("direct_memory", memoryMetrics.getDirectMemory()));
        simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>("mapped_cache", memoryMetrics.getMappedCache()));
        simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>("heap", memoryMetrics.getHeapUsed()));
        simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>("non_heap", memoryMetrics.getNonHeapUsed()));
        simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>("cpu", processMetrics.getCpuUsagePercent()));
        simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>("memory_total", processMetrics.getMemoryUsage()));
        LogUtil.println("resources", true, simpleEntryList);
    }

    private void processBufferPoolMetrics() {
        List<BufferPoolMetrics> bufferPoolMetricsList = bufferPoolCollector.collect();
        List<Entry<String, Object>> simpleEntryList = new ArrayList<>();
        for (BufferPoolMetrics bufferPoolMetrics : bufferPoolMetricsList) {
            simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>(bufferPoolMetrics.getName() + "_buffer_pool_count", bufferPoolMetrics.getCount()));
            simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>(bufferPoolMetrics.getName() + "_buffer_pool_memory_used", bufferPoolMetrics.getMemoryUsed()));
            simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>(bufferPoolMetrics.getName() + "_buffer_pool_memory_capacity", bufferPoolMetrics.getMemoryCapacity()));
        }
        LogUtil.println("resources", true, simpleEntryList);
    }

    private void processJVMMetrics() {
        List<Entry<String, Object>> simpleEntryList = new ArrayList<>();
        JVMGcMetrics jvmGcMetrics = gcCollector.collect();
        ClassMetrics classMetrics = classCollector.collect();
        simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>("young_gc_count", jvmGcMetrics.getYoungGcCount()));
        simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>("young_gc_time", jvmGcMetrics.getYoungGcTime()));
        simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>("avg_young_gc_time", jvmGcMetrics.getAvgYoungGcTime()));
        simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>("full_gc_count", jvmGcMetrics.getFullGcCount()));
        simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>("full_gc_time", jvmGcMetrics.getFullGcTime()));
        simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>("class_total", classMetrics.getTotal()));
        simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>("class_loaded", classMetrics.getLoaded()));
        simpleEntryList.add(new AbstractMap.SimpleEntry<String, Object>("class_unloaded", classMetrics.getUnloaded()));
        LogUtil.println("resources", true, simpleEntryList);
    }

    private void processThreadMetrics() {
        ThreadMetrics threadMetrics = threadCollector.collect();
        LogUtil.println("resources", true, Arrays.asList(
                new AbstractMap.SimpleEntry<String, Object>("thread_total_started", threadMetrics.getTotalStarted()),
                new AbstractMap.SimpleEntry<String, Object>("thread_active", threadMetrics.getActive()),
                new AbstractMap.SimpleEntry<String, Object>("thread_daemon", threadMetrics.getDaemon()),
                new AbstractMap.SimpleEntry<String, Object>("thread_runnable", threadMetrics.getRunnable()),
                new AbstractMap.SimpleEntry<String, Object>("thread_blocked", threadMetrics.getBlocked()),
                new AbstractMap.SimpleEntry<String, Object>("thread_waiting", threadMetrics.getWaiting()),
                new AbstractMap.SimpleEntry<String, Object>("thread_timed_waiting", threadMetrics.getTimedWaiting()),
                new AbstractMap.SimpleEntry<String, Object>("thread_terminated", threadMetrics.getTerminated()),
                new AbstractMap.SimpleEntry<String, Object>("thread_peak", threadMetrics.getPeak()),
                new AbstractMap.SimpleEntry<String, Object>("thread_news", threadMetrics.getNews())
        ));
    }
}
