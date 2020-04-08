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
package com.yametech.yangjian.agent.core.jvm;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.jvm.collector.BufferPoolCollector;
import com.yametech.yangjian.agent.core.jvm.collector.ClassCollector;
import com.yametech.yangjian.agent.core.jvm.collector.GcCollector;
import com.yametech.yangjian.agent.core.jvm.collector.MemoryCollector;
import com.yametech.yangjian.agent.core.jvm.collector.MemoryPoolCollector;
import com.yametech.yangjian.agent.core.jvm.collector.ProcessCollector;
import com.yametech.yangjian.agent.core.jvm.collector.ThreadCollector;
import com.yametech.yangjian.agent.core.jvm.metrics.BufferPoolMetrics;
import com.yametech.yangjian.agent.core.jvm.metrics.ClassMetrics;
import com.yametech.yangjian.agent.core.jvm.metrics.JVMGcMetrics;
import com.yametech.yangjian.agent.core.jvm.metrics.MemoryMetrics;
import com.yametech.yangjian.agent.core.jvm.metrics.MemoryPoolMetrics;
import com.yametech.yangjian.agent.core.jvm.metrics.ProcessMetrics;
import com.yametech.yangjian.agent.core.jvm.metrics.ThreadMetrics;
import com.yametech.yangjian.agent.core.metric.MetricData;
import com.yametech.yangjian.agent.core.report.ReportManage;

/**
 * @author zcn
 * @date: 2019-10-25
 **/
public class JVMMetricsSchedule implements IAppStatusListener, ISchedule, IConfigReader {
    private static final ILogger LOG = LoggerFactory.getLogger(JVMMetricsSchedule.class);
    private static final String CONFIG_KEY = "jvmMetricOutput.interval";
    private static final String BASE_PATH = "status/";
    private IReportData report = ReportManage.getReport("JVMMetricsSchedule");
    private BufferPoolCollector bufferPoolCollector;
    private GcCollector gcCollector;
    private ThreadCollector threadCollector;
    private MemoryCollector memoryCollector;
    private MemoryPoolCollector memoryPoolCollector;
    private ProcessCollector processCollector;
    private ClassCollector classCollector;
    private int interval = 5;

    @Override
    public Set<String> configKey() {
        return new HashSet<>(Arrays.asList(CONFIG_KEY));
    }

    @Override
    public void configKeyValue(Map<String, String> kv) {
        if (kv == null) {
            return;
        }
        
        String intervalStr = kv.get(CONFIG_KEY);
    	if(intervalStr != null) {
    		try {
    			interval = Integer.parseInt(intervalStr);
            } catch(Exception e) {
            	LOG.warn("{}配置错误：{}", CONFIG_KEY, intervalStr);
            }
    	}
    }
    
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
        return interval;
    }

    @Override
    public void execute() {
        try {
            processMemoryMetrics();
            processBufferPoolMetrics();
            processJVMMetrics();
            processThreadMetrics();
        } catch (Exception e) {
        	LOG.error(e, "collect jvm metrics error");
        }
    }

    @Override
    public boolean shutdown(Duration duration) {
        return true;
    }

    private void processMemoryMetrics() {
        Map<String, Object> params = new HashMap<>();
        MemoryMetrics memoryMetrics = memoryCollector.collect();
        ProcessMetrics processMetrics = processCollector.collect();
        List<MemoryPoolMetrics> memoryPoolMetricsList = memoryPoolCollector.collect();
        for (MemoryPoolMetrics memoryPoolMetrics : memoryPoolMetricsList) {
        	params.put(memoryPoolMetrics.getType().name().toLowerCase(), memoryPoolMetrics.getUsed());
        }
        params.put("direct_memory", memoryMetrics.getDirectMemory());
        params.put("mapped_cache", memoryMetrics.getMappedCache());
        params.put("heap", memoryMetrics.getHeapUsed());
        params.put("non_heap", memoryMetrics.getNonHeapUsed());
        params.put("cpu", processMetrics.getCpuUsagePercent());
        params.put("memory_total", processMetrics.getMemoryUsage());
    	report.report(MetricData.get(null, BASE_PATH + Constants.Status.RESOURCES, params));
    }

    private void processBufferPoolMetrics() {
        List<BufferPoolMetrics> bufferPoolMetricsList = bufferPoolCollector.collect();
        Map<String, Object> params = new HashMap<>();
        for (BufferPoolMetrics bufferPoolMetrics : bufferPoolMetricsList) {
            params.put(bufferPoolMetrics.getName() + "_buffer_pool_count", bufferPoolMetrics.getCount());
            params.put(bufferPoolMetrics.getName() + "_buffer_pool_memory_used", bufferPoolMetrics.getMemoryUsed());
            params.put(bufferPoolMetrics.getName() + "_buffer_pool_memory_capacity", bufferPoolMetrics.getMemoryCapacity());
        }
        report.report(MetricData.get(null, BASE_PATH + Constants.Status.RESOURCES, params));
    }

    private void processJVMMetrics() {
    	Map<String, Object> params = new HashMap<>();
        JVMGcMetrics jvmGcMetrics = gcCollector.collect();
        ClassMetrics classMetrics = classCollector.collect();
        params.put("young_gc_count", jvmGcMetrics.getYoungGcCount());
        params.put("young_gc_time", jvmGcMetrics.getYoungGcTime());
        params.put("avg_young_gc_time", jvmGcMetrics.getAvgYoungGcTime());
        params.put("full_gc_count", jvmGcMetrics.getFullGcCount());
        params.put("full_gc_time", jvmGcMetrics.getFullGcTime());
        params.put("class_total", classMetrics.getTotal());
        params.put("class_loaded", classMetrics.getLoaded());
        params.put("class_unloaded", classMetrics.getUnloaded());
        report.report(MetricData.get(null, BASE_PATH + Constants.Status.RESOURCES, params));
    }

    private void processThreadMetrics() {
        ThreadMetrics threadMetrics = threadCollector.collect();
        Map<String, Object> params = new HashMap<>();
        params.put("thread_total_started", threadMetrics.getTotalStarted());
        params.put("thread_active", threadMetrics.getActive());
        params.put("thread_daemon", threadMetrics.getDaemon());
        params.put("thread_runnable", threadMetrics.getRunnable());
        params.put("thread_blocked", threadMetrics.getBlocked());
        params.put("thread_waiting", threadMetrics.getWaiting());
        params.put("thread_timed_waiting", threadMetrics.getTimedWaiting());
        params.put("thread_terminated", threadMetrics.getTerminated());
        params.put("thread_peak", threadMetrics.getPeak());
        params.put("thread_news", threadMetrics.getNews());
        report.report(MetricData.get(null, BASE_PATH + Constants.Status.RESOURCES, params));
    }
}
