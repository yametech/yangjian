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

import java.util.LinkedList;
import java.util.List;

/**
 * @author zcn
 * @date: 2019-10-25
 */
public class NoopMemoryPoolAccessor implements IMemoryPoolAccessor {
    @Override
    public List<MemoryPoolMetrics> getMemoryPoolList() {
        List<MemoryPoolMetrics> poolList = new LinkedList<>();
        poolList.add(new MemoryPoolMetrics(MemoryPoolType.CODE_CACHE));
        poolList.add(new MemoryPoolMetrics(MemoryPoolType.METASPACE));
        poolList.add(new MemoryPoolMetrics(MemoryPoolType.EDEN));
        poolList.add(new MemoryPoolMetrics(MemoryPoolType.OLDGEN));
        poolList.add(new MemoryPoolMetrics(MemoryPoolType.PERMGEN));
        poolList.add(new MemoryPoolMetrics(MemoryPoolType.SURVIVOR));
        return poolList;
    }
}
