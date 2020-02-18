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

package com.yametech.yangjian.agent.util.eventbus.process;


import java.util.function.Consumer;

import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.dsl.Disruptor;

public class DiscardEventBus<T> extends EventBus<T> {
	
	public DiscardEventBus(Disruptor<T> disruptor) {
		super(disruptor);
	}
    /**
     * 发布消息
     * @param consumer   发布消费者，用于初始化事件值（事件实例是共用的），注意：如果缓存满了，consumer.accept的参数会是null，此时该数据会被丢弃
     */
	@Override
    public boolean publish(Consumer<T> consumer) {
    	long sequence = -1;
		try {
			sequence = ringBuffer.tryNext();
		} catch (InsufficientCapacityException e) {
			consumer.accept(null);
			return false;
		}
		return publish(consumer, sequence);
    }

}
