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
package com.yametech.yangjian.agent.core.jvm.collector;

import com.yametech.yangjian.agent.core.jvm.metrics.CPUMetrics;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;


/**
 * @author zcn
 * @date: 2019-10-17
 */
public final class CPUCollector implements IMetricsCollector {

    private OperatingSystemMXBean operatingSystemMXBean;
    private int processor;
    private long lastCpuTime;
    private long lastTime;

    public CPUCollector() {
        this.operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.processor = operatingSystemMXBean.getAvailableProcessors();

        this.lastCpuTime = operatingSystemMXBean.getProcessCpuTime();
        this.lastTime = System.nanoTime();
    }

    @Override
    public CPUMetrics collect() {
        return new CPUMetrics(calculateCpuUsage(), processor);
    }

    private double calculateCpuUsage() {
        long cpuTime = operatingSystemMXBean.getProcessCpuTime();
        long now = System.nanoTime();
        long cpuCost = cpuTime - lastCpuTime;
        try {
            return cpuCost * 1.0d / ((now - lastTime) * processor) * 100;
        } finally {
            this.lastCpuTime = cpuTime;
            this.lastTime = now;
        }
    }
}

