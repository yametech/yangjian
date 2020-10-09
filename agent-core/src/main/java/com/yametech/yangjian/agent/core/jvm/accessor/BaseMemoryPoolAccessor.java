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
package com.yametech.yangjian.agent.core.jvm.accessor;

import com.yametech.yangjian.agent.core.jvm.metrics.MemoryPoolMetrics;
import com.yametech.yangjian.agent.core.jvm.common.MemoryPoolType;

import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zcn
 * @date: 2019-10-25
 */
public abstract class BaseMemoryPoolAccessor implements IMemoryPoolAccessor {

    private List<MemoryPoolMXBean> beans;

    BaseMemoryPoolAccessor(List<MemoryPoolMXBean> beans) {
        this.beans = beans;
    }

    @Override
    public List<MemoryPoolMetrics> getMemoryPoolList() {
        List<MemoryPoolMetrics> poolList = new LinkedList<>();
        for (MemoryPoolMXBean pool : beans) {
            String name = pool.getName();
            MemoryPoolType memoryPoolType = getPoolType(name);
            if(memoryPoolType == null) {
                continue;
            }
            MemoryUsage memoryUsage = pool.getUsage();
            poolList.add(new MemoryPoolMetrics(
                    memoryPoolType,
                    memoryUsage.getInit() >> 10,
                    memoryUsage.getMax() >> 10,
                    memoryUsage.getUsed() >> 10,
                    memoryUsage.getCommitted() >> 10));
        }
        return poolList;
    }

    private MemoryPoolType getPoolType(String name) {
        MemoryPoolType poolType = null;
        if (has(getPermNames(), name)) {
            poolType = MemoryPoolType.PERMGEN;
        } else if (has(getCodeCacheNames(), name)) {
            poolType = MemoryPoolType.CODE_CACHE;
        } else if (has(getEdenNames(), name)) {
            poolType = MemoryPoolType.EDEN;
        } else if (has(getOldNames(), name)) {
            poolType = MemoryPoolType.OLDGEN;
        } else if (has(getSurvivorNames(), name)) {
            poolType = MemoryPoolType.SURVIVOR;
        } else if (has(getMetaspaceNames(), name)) {
            poolType = MemoryPoolType.METASPACE;
        }
        return poolType;
    }

    private boolean has(String[] names, String name) {
        for (String n : names) {
            if (n.equals(name)) {
                return true;
            }
        }
        return false;
    }

    protected abstract String[] getPermNames();

    protected abstract String[] getCodeCacheNames();

    protected abstract String[] getEdenNames();

    protected abstract String[] getOldNames();

    protected abstract String[] getSurvivorNames();

    protected abstract String[] getMetaspaceNames();
}
