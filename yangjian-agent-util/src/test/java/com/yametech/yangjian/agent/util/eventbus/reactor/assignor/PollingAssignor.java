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
package com.yametech.yangjian.agent.util.eventbus.reactor.assignor;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 轮询分配
 * @param <T>
 */
public class PollingAssignor<T> implements MultiThreadAssignor<T> {
    private AtomicLong counter = new AtomicLong(Long.MIN_VALUE);

    @Override
    public int threadNum(T msg, int totalThreadNum) {
        return (int)(Math.abs(counter.getAndIncrement()) % totalThreadNum);
    }
}
