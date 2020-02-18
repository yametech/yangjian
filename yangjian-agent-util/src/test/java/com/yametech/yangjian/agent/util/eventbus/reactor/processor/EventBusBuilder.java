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

package com.yametech.yangjian.agent.util.eventbus.reactor.processor;


import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.yametech.yangjian.agent.util.eventbus.reactor.consume.BaseConsume;
import com.yametech.yangjian.agent.util.eventbus.reactor.regist.IRegist;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import reactor.core.publisher.TopicProcessor;
import reactor.util.concurrent.WaitStrategy;

public class EventBusBuilder {
	// 名称，标识生产者
	private String name;
    private boolean parallelPublish = true;
    private int bufferSize = 8;
    private boolean asyncConsume = true;
    private ThreadPoolExecutor executor;
    // 异步生产或者消费时，如果速度不对称时的等待策略
    private WaitStrategy waitStrategy = WaitStrategy.blocking();
//    // 数据消费是否顺序（按照生产的顺序消费）
//    private boolean ordered = true;
    // 多线程消费时如何分配消息与线程的对应关系，如果不指定该值，则乱序消费
//    private MultiThreadAssignor assignor;
    // 注册方式，如果希望使用spring bean管理实例，则自己实现IRegister
    private IRegist register;

    private EventBusBuilder(boolean asyncConsume) {
        this.asyncConsume = asyncConsume;
    }

    /**
     * 创建异步消费事件还是同步消费
     * @param asyncConsume	是否异步消费数据
     */
    public static EventBusBuilder asyncConsume(boolean asyncConsume) {
        return new EventBusBuilder(asyncConsume);
    }
    
    public EventBusBuilder name(String name) {
    	this.name = name;
    	return this;
    }

    /**
     * 是否多线程发布数据
     * @param parallelPublish
     * @return
     */
    public EventBusBuilder parallelPublish(boolean parallelPublish) {
        this.parallelPublish = parallelPublish;
        return this;
    }

    /**
     * 缓存池大小，必须为2的N次幂
     * @param bufferSize
     * @return
     */
    public EventBusBuilder bufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    /**
     * 异步消费时的消费线程池
     * 例如：此处生成的EventBus调用addSubscriber添加了5个消费者（每个消费者配置了一个线程），如果线程池为1，则5个消费者串行执行，如果线程池为5，则为并行执行，
     * 		如果线程个数大于消费者总数，则超出的线程不会使用；
     * 默认为：Executors.newCachedThreadPool(threadFactory);
     * @param executor
     * @return
     */
    public EventBusBuilder executor(ThreadPoolExecutor executor) {
        this.executor = executor;
        return this;
    }

    public EventBusBuilder waitStrategy(WaitStrategy waitStrategy) {
        this.waitStrategy = waitStrategy;
        return this;
    }

    public EventBusBuilder register(IRegist register) {
        this.register = register;
        return this;
    }

//    public EventBusBuilder ordered(boolean ordered) {
//        this.ordered = ordered;
//        return this;
//    }

//    public EventBusBuilder executor(MultiThreadAssignor assignor) {
//        this.assignor = assignor;
//        return this;
//    }

    @SuppressWarnings("unchecked")
    public <T> EventBus<T> build() {
        EventBus<T> eventBus = null;
        if(asyncConsume) {
            TopicProcessor<T> processor = TopicProcessor.<T>builder()
            		.name(name)
                    .executor(executor)// 这个线程池仅用于限制subscribe的个数不能超过配置数量，与普通线程池随着堆积量扁阔扩容不一样
                    .share(parallelPublish)// 如果topicProcessor用于多线程发布事件，这里必须配置为true
                    .bufferSize(bufferSize)//2^13,必须传入2的方次幂,缓存大小，没超出缓存大小时多个subscribe可以并行执行，超出后onNext所有subscribe接收数据都会被阻塞
                    .waitStrategy(waitStrategy)
                    .build();
            processor.sink(OverflowStrategy.LATEST);
            eventBus = new TopicEventBus<T>(processor, name);
        } else {
            eventBus = new DirectEventBus<T>(DirectProcessor.create(), name);
        }
        if(register != null) {
            List<BaseConsume<Object>> consumes =  register.regist();
            for(BaseConsume<?> consume : consumes) {
                eventBus.addSubscriber((BaseConsume<T>)consume);
            }
        }
        return eventBus;
    }

}
