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

import com.yametech.yangjian.agent.core.jvm.collector.CPUCollector;
import com.yametech.yangjian.agent.core.jvm.collector.ProcessCollector;
import com.yametech.yangjian.agent.core.jvm.metrics.CPUMetrics;
import com.yametech.yangjian.agent.core.jvm.metrics.ProcessMetrics;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author dengliming
 * @date 2020/11/3
 */
public class CPUCollectorTest {

    @Test
    public void test() {
        CPUCollector cpuCollector = new CPUCollector();
        CPUMetrics cpuMetrics = cpuCollector.collect();
        assertNotNull(cpuMetrics);
        assertTrue(cpuMetrics.getUsagePercent() >= 0);
        assertTrue(cpuMetrics.getAvailableProcessors() > 0);
    }

    @Test
    public void testProcessCollector() {
        ProcessCollector processCollector = new ProcessCollector();
        ProcessMetrics processMetrics = processCollector.collect();
        assertNotNull(processMetrics);
        assertTrue(processMetrics.getCpuUsagePercent() >= 0);
    }
}
