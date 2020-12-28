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
package com.yametech.yangjian.agent.core.jvm.metrics;

/**
 * @author zcn
 * @date: 2019-10-21
 */
public class ProcessMetrics implements IMetrics {

    private double cpuUsagePercent = 0.0d;
    /**
     * 进程内存使用
     */
    private double memoryUsage = 0.0d;
    /**
     * 系统总内存
     */
    private long sysMemTotal = 0L;

    public double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public void setCpuUsagePercent(double cpuUsagePercent) {
        this.cpuUsagePercent = cpuUsagePercent;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public long getSysMemTotal() {
        return sysMemTotal;
    }

    public void setSysMemTotal(long sysMemTotal) {
        this.sysMemTotal = sysMemTotal;
    }
}
