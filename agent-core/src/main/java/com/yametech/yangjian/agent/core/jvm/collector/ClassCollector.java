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

import com.yametech.yangjian.agent.core.jvm.metrics.ClassMetrics;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

/**
 * @author dengliming
 * @date 2019/12/27
 */
public final class ClassCollector implements IMetricsCollector {

    private ClassLoadingMXBean classLoadingMXBean;

    public ClassCollector() {
        classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
    }

    @Override
    public ClassMetrics collect() {
        return new ClassMetrics(classLoadingMXBean.getTotalLoadedClassCount(), classLoadingMXBean.getLoadedClassCount(), classLoadingMXBean.getUnloadedClassCount());
    }
}
