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

import com.yametech.yangjian.agent.core.jvm.metrics.BufferPoolMetrics;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dengliming
 * @date 2019/12/27
 */
public final class BufferPoolCollector implements IMetricsCollector<List<BufferPoolMetrics>> {

    private List<BufferPoolMXBean> bufferPoolMXBeans;

    public BufferPoolCollector() {
        bufferPoolMXBeans = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
    }

    @Override
    public List<BufferPoolMetrics> collect() {
        List<BufferPoolMetrics> metricsList = new ArrayList<>(bufferPoolMXBeans.size());
        for (BufferPoolMXBean bufferPoolMXBean : bufferPoolMXBeans) {
            metricsList.add(new BufferPoolMetrics(
                    bufferPoolMXBean.getName(),
                    bufferPoolMXBean.getCount(),
                    bufferPoolMXBean.getMemoryUsed() >> 10,
                    bufferPoolMXBean.getTotalCapacity() >> 10));
        }
        return metricsList;
    }
}
