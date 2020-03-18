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
package com.yametech.yangjian.agent.util.eventbus.reactor.consume;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.yametech.yangjian.agent.util.eventbus.reactor.assignor.MultiThreadAssignor;

public interface BaseConsume<T> extends Consumer<T>,Predicate<T> {

	@Override
	default boolean test(T t) {
		return true;
	}
	
    /**
     * 当前消费者的并行执行数量，如果大于1，实例必须线程安全
     * @return
     */
    default int parallelism() {
        return 1;
    }

    /**
     * 获取多线程消费时消息的分配方式，如果并行数为1，则此处可返回null
     * @return
     */
    default MultiThreadAssignor<T> assignor() {
        return null;
    }
    
    /**
     * 定义消费名称
     * @return
     */
    default String name() {
    	return "";
    }
}
