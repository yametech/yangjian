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

package com.yametech.yangjian.agent.core.jvm.metrics;

import com.yametech.yangjian.agent.core.jvm.common.MemoryPoolType;

/**
 * JVM各内存区域资源占用
 *
 * @author dengliming
 * @date 2019/12/27
 */
public class MemoryPoolMetrics implements IMetrics {

    private MemoryPoolType type;
    /**
     * 以下单位都是KB
     */
    private long init = 0L;
    private long max = 0L;
    private long used = 0L;
    private long commited = 0L;

    public MemoryPoolMetrics(MemoryPoolType type) {
        this.type = type;
    }

    public MemoryPoolMetrics(MemoryPoolType type, long init, long max, long used, long commited) {
        this.type = type;
        this.init = init;
        this.max = max;
        this.used = used;
        this.commited = commited;
    }

    public MemoryPoolType getType() {
        return type;
    }

    public long getInit() {
        return init;
    }

    public long getMax() {
        return max;
    }

    public long getUsed() {
        return used;
    }

    public long getCommited() {
        return commited;
    }
}
