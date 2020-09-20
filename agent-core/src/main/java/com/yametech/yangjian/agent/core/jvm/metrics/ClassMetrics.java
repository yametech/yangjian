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
 * @author dengliming
 * @date 2019/12/27
 */
public class ClassMetrics implements IMetrics {
    /**
     * 自JVM开始执行到目前已经加载的类的总数
     */
    private final long total;
    /**
     * 当前加载到JVM中的类的数量
     */
    private final long loaded;
    /**
     * 自JVM开始执行到目前已经卸载的类的总数
     */
    private final long unloaded;

    public ClassMetrics(long total, long loaded, long unloaded) {
        this.total = total;
        this.loaded = loaded;
        this.unloaded = unloaded;
    }

    public long getTotal() {
        return total;
    }

    public long getLoaded() {
        return loaded;
    }

    public long getUnloaded() {
        return unloaded;
    }
}
