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

import com.yametech.yangjian.agent.core.jvm.accessor.*;
import com.yametech.yangjian.agent.core.jvm.accessor.*;
import com.yametech.yangjian.agent.core.jvm.metrics.MemoryPoolMetrics;
import com.yametech.yangjian.agent.core.log.ILogger;
import com.yametech.yangjian.agent.core.log.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;

/**
 * @author zcn
 * @date: 2019-10-25
 **/
public final class MemoryPoolCollector implements IMetricsCollector {

    private static final ILogger logger = LoggerFactory.getLogger(MemoryPoolCollector.class);
    private List<MemoryPoolMXBean> beans;
    private IMemoryPoolAccessor accessor;

    public MemoryPoolCollector() {
        beans = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean bean : beans) {
            IMemoryPoolAccessor a = getAccessor(bean.getName());
            if (a != null) {
                this.accessor = a;
                break;
            }
        }

        if (accessor == null) {
            logger.error("can not match memory pool accessor, use default memory pool accessor");
            accessor = new NoopMemoryPoolAccessor();
        }
    }

    @Override
    public List<MemoryPoolMetrics> collect() {
        return accessor.getMemoryPoolList();
    }

    public IMemoryPoolAccessor getAccessor(String name) {
        if (name.indexOf("PS") > -1) {
            // -XX:+UseParallelOldGC
            return new ParallelMemoryPoolAccessor(beans);
        } else if (name.indexOf("CMS") > -1) {
            //  -XX:+UseConcMarkSweepGC
            return new GMSMemoryPoolAccessor(beans);
        } else if (name.indexOf("G1") > -1) {
            //-XX:+UseG1GC
            return new G1MemoryPoolAccessor(beans);
        } else if (name.equals("Survivor Space")) {
            // -XX:+UseSerialGC
            return new SerialMemoryPoolAccessor(beans);
        }
        return null;
    }

}
