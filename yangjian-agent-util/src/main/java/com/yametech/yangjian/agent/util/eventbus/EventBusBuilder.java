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
package com.yametech.yangjian.agent.util.eventbus;


import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

import com.yametech.yangjian.agent.util.eventbus.consume.BaseConfigConsume;
import com.yametech.yangjian.agent.util.eventbus.consume.BaseConsume;
import com.yametech.yangjian.agent.util.eventbus.consume.ConsumeConfig;
import com.yametech.yangjian.agent.util.eventbus.consume.ConsumeFactory;
import com.yametech.yangjian.agent.util.eventbus.process.DiscardEventBus;
import com.yametech.yangjian.agent.util.eventbus.process.EventBus;
import com.yametech.yangjian.agent.util.eventbus.process.base.EventHandlerAdapter;
import com.yametech.yangjian.agent.util.eventbus.process.base.MultiThreadEventHandlerAdapter;
import com.yametech.yangjian.agent.util.eventbus.process.base.WorkerHandlerAdapter;
import com.yametech.yangjian.agent.util.eventbus.regist.IRegist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class EventBusBuilder {
	// 名称，标识生产者
//	private String name;
	private boolean discardFull = false;
//	private Consumer<?> discardConsumer;
    private boolean parallelPublish = true;
    private int bufferSize = 1 << 5;
//    private boolean asyncConsume = true;
    private ThreadFactory threadFactory = Executors.defaultThreadFactory();
    private WaitStrategy waitStrategy = new BlockingWaitStrategy();
    private IRegist register;

    private EventBusBuilder(IRegist register) {
        this.register = register;
    }

    /**
     * 注册方式，如果希望使用spring bean管理实例，则自己实现IRegister
     * @param register
     * @return
     */
    public static EventBusBuilder create(IRegist register) {
    	if(register == null) {
    		throw new RuntimeException("register不能为null");
    	}
        return new EventBusBuilder(register);
    }
    
    /**
     * 设置是否在buffer满后丢弃最新数据，注意：如果有N个消费者，最慢的消费者有bufferSize个未消费时，就会导致丢弃最新数据，其他消费者也接受不到该数据，同理：该值为false时，最慢的消费者可能导致生产阻塞，所有消费者都无法消费消息
     * @param discardFull	true：当buffer满后丢弃最新产生的数据；false：阻塞生产者发布消息，直到消费
     * @return
     */
    public EventBusBuilder setDiscardFull(boolean discardFull) {
		this.discardFull = discardFull;
		return this;
	}
    
    /**
     * 如果discardFull设置未true，可通过设置该值，监听丢弃的事件
     * @param discardConsumer
     * @return
     */
//    public EventBusBuilder setDiscardConsumer(Consumer<?> discardConsumer) {
//		this.discardConsumer = discardConsumer;
//		return this;
//	}
    
    /**
     * 
     * @param name	名称，标识生产者
     * @return
     */
//    public EventBusBuilder name(String name) {
//    	this.name = name;
//    	return this;
//    }
    
    /**
     * 
     * @param parallelPublish	是否多线程发布数据
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
     * 线程创建工厂类
     * @param threadFactory
     * @return
     */
    public EventBusBuilder executor(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    /**
     * 异步生产或者消费时，如果速度不对称时的等待策略
     * @param waitStrategy
     * @return
     */
    public EventBusBuilder waitStrategy(WaitStrategy waitStrategy) {
        this.waitStrategy = waitStrategy;
        return this;
    }

	public <T> EventBus<T> build(Class<T> cls) {
		return build(cls, new ExceptionHandler<T>() {
			@Override
			public void handleEventException(Throwable ex, long sequence, T event) {
				//log.warn("消费事件异常：{}, {}", sequence, event, ex);
			}

			@Override
			public void handleOnStartException(Throwable ex) {
				//log.warn("启动disruptor异常", ex);
			}

			@Override
			public void handleOnShutdownException(Throwable ex) {
				//log.warn("关闭disruptor异常", ex);
			}
		});
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> EventBus<T> build(Class<T> cls, ExceptionHandler<T> exceptionHandler) {
    	try {
			cls.getConstructor();
		} catch (NoSuchMethodException | SecurityException e1) {
			throw new RuntimeException(cls + "必须包含public无参构造方法");
		}
		
		EventFactory<T> factory = () -> {
			try {
				return cls.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				return null;
			}
		};
		if (discardFull) {
			return new DiscardEventBus(getInstance(factory, exceptionHandler));
		} else {
			return new EventBus<>(getInstance(factory, exceptionHandler));
		}
    }

    @SuppressWarnings("unchecked")
	private <T> Disruptor<T> getInstance(EventFactory<T> factory, ExceptionHandler<T> exceptionHandler) {
    	ProducerType type = parallelPublish ? ProducerType.MULTI : ProducerType.SINGLE;
		Disruptor<T> disruptor = new Disruptor<>(factory, bufferSize, threadFactory, type, waitStrategy);
		List<? extends ConsumeConfig<?>> consumes = register.regist();
		if(consumes == null || consumes.isEmpty()) {
			throw new RuntimeException("未配置消费实例");
		}
		List<EventHandlerAdapter<T>> singleThreadHandlers = new ArrayList<>();
		Map<ConsumeConfig<T>, List<WorkerHandlerAdapter<T>>> multiThreadWorkHandlers = new HashMap<>();
		Map<ConsumeConfig<T>, List<MultiThreadEventHandlerAdapter<T>>> multiThreadHandlers = new HashMap<>();
		for (int i = 0; i < consumes.size(); i++) {
			ConsumeConfig<T> consumeConfig = (ConsumeConfig<T>) consumes.get(i);
			if(consumeConfig.parallelism() < 1) {
				throw new RuntimeException("consume并发个数配置错误（必须大于0）");
			}
			if(consumeConfig.parallelism() == 1) {
				singleThreadHandlers.add(new EventHandlerAdapter<>((BaseConsume<T>) getConsume(consumeConfig)));
			} else {
				if(consumeConfig.assignor() == null) {
					List<WorkerHandlerAdapter<T>> handlers = new ArrayList<>();
					for(int j = 0; j < consumeConfig.parallelism(); j++) {
						handlers.add(new WorkerHandlerAdapter<>((BaseConsume<T>) getConsume(consumeConfig)));
					}
					multiThreadWorkHandlers.put(consumeConfig, handlers);
				} else {
					List<MultiThreadEventHandlerAdapter<T>> handlers = new ArrayList<>();
					for(int j = 0; j < consumeConfig.parallelism(); j++) {
						handlers.add(new MultiThreadEventHandlerAdapter<>((BaseConsume<T>) getConsume(consumeConfig), consumeConfig.parallelism(), j, consumeConfig.assignor()));
					}
					multiThreadHandlers.put(consumeConfig, handlers);
				}
			}
		}
		EventHandlerGroup<T> group = null;
		if(!singleThreadHandlers.isEmpty()) {
			group = disruptor.handleEventsWith(singleThreadHandlers.toArray(new EventHandlerAdapter[0]));
		}
		if(!multiThreadWorkHandlers.isEmpty()) {
			for(List<WorkerHandlerAdapter<T>> handlers : multiThreadWorkHandlers.values()) {
				if(group != null) {
					group.and(disruptor.handleEventsWithWorkerPool(handlers.toArray(new WorkerHandlerAdapter[0])));
				} else {
					group = disruptor.handleEventsWithWorkerPool(handlers.toArray(new WorkerHandlerAdapter[0]));
				}
			}
		}
		if(!multiThreadHandlers.isEmpty()) {
			for(List<MultiThreadEventHandlerAdapter<T>> handlers : multiThreadHandlers.values()) {
				if(group != null) {
					group.and(disruptor.handleEventsWith(handlers.toArray(new MultiThreadEventHandlerAdapter[0])));
				} else {
					group = disruptor.handleEventsWith(handlers.toArray(new MultiThreadEventHandlerAdapter[0]));
				}
			}
		}
		disruptor.setDefaultExceptionHandler(exceptionHandler);
		return disruptor;
	}
    
	private BaseConsume<?> getConsume(ConsumeConfig<?> consumeConfig) {
    	if(consumeConfig instanceof BaseConfigConsume) {
    		return (BaseConsume<?>) consumeConfig;
    	} else if(consumeConfig instanceof ConsumeFactory) {
    		return ((ConsumeFactory<?>) consumeConfig).getConsume();
    	} else {
    		throw new RuntimeException("错误的IRegist实现");
    	}
    }

}
