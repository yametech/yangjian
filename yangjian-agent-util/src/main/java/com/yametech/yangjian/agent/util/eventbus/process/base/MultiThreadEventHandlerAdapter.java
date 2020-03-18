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
package com.yametech.yangjian.agent.util.eventbus.process.base;

import com.yametech.yangjian.agent.util.eventbus.assignor.MultiThreadAssignor;
import com.yametech.yangjian.agent.util.eventbus.consume.BaseConsume;

public class MultiThreadEventHandlerAdapter<T> extends EventHandlerAdapter<T> {
    private MultiThreadAssignor<T> assignor;
    private int threadIndex;
    private int parallelism;

    public MultiThreadEventHandlerAdapter(BaseConsume<T> consume, int parallelism, int threadIndex, MultiThreadAssignor<T> assignor) {
        super(consume);
        this.threadIndex = threadIndex;
        this.assignor = assignor;
        this.parallelism = parallelism;
    }

    @Override
    public void onEvent(T event, long sequence, boolean endOfBatch) throws Exception {
        if (threadIndex == assignor.threadNum(event, parallelism) && consume.test(event)) {
            consume.accept(event);
        }
    }

}
