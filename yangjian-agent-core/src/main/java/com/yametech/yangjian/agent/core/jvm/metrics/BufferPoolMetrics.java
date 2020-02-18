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

/**
 * @author dengliming
 * @date 2019/12/27
 */
public class BufferPoolMetrics implements IMetrics {
    /**
     * 缓存池名称
     */
    private final String name;
    /**
     * 缓存池中buffer的数量
     */
    private final long count;
    /**
     * JVM用于此缓冲池的内存估计值（KB）
     */
    private final long memoryUsed;
    /**
     * 缓存池中所有buffer的总容量估计值（KB）
     */
    private final long memoryCapacity;

    public BufferPoolMetrics(String name, long count, long memoryUsed, long memoryCapacity) {
        this.name = name;
        this.count = count;
        this.memoryUsed = memoryUsed;
        this.memoryCapacity = memoryCapacity;
    }

    public String getName() {
        return name;
    }

    public long getCount() {
        return count;
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public long getMemoryCapacity() {
        return memoryCapacity;
    }
}
