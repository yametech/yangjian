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

package com.yametech.yangjian.agent.core.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 日志打印速率控制
 *
 * @author dengliming
 * @date 2019/11/19
 */
public class LogRateLimiter implements IRateLimiter {
    /**
     * 控制每秒最大请求数
     */
    private int maxLimit;
    private int slotSize = 4;
    /**
     * 最近slotSize秒请求次数（每秒创建占一个槽）
     */
    private AtomicLong[] slotCounter;

    public LogRateLimiter(int maxLimit) {
        slotCounter = new AtomicLong[slotSize];
        for (int i = 0; i < slotSize; i++) {
            slotCounter[i] = new AtomicLong(0);
        }
        this.maxLimit = maxLimit;
    }

    /**
     * 尝试获取许可
     *
     * @return
     */
    @Override
    public boolean tryAcquire() {
        // 根据当前秒算出所属的槽
        long timeSeconds = System.currentTimeMillis() / 1000;
        int index = ((int) timeSeconds) & (slotSize - 1);
        AtomicLong counter = slotCounter[index];
        // 当前槽位接收最小值
        long min = timeSeconds * maxLimit;
        // 当前槽位接收最大值
        long max = (timeSeconds + 1) * maxLimit;
        long current = counter.get();
        // 如果该槽当前值比当前时间戳计算的min值还小则重置
        while (current < min) {
            if (!counter.compareAndSet(current, min)) {
                current = counter.get();
            }
        }

        if (counter.get() + 1 > max) {
            return false;
        }

        if (counter.incrementAndGet() > max) {
            // 防止瞬时并发累加超过一轮影响其他秒槽
            counter.decrementAndGet();
            return false;
        }
        return true;
    }

    @Override
    public void release() {
        // do nothing
    }
}
