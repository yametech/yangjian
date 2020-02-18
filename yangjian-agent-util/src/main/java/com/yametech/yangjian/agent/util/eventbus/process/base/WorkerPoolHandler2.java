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

package com.yametech.yangjian.agent.util.eventbus.process.base;

//public class WorkerPoolHandler2<T> extends EventBus<T> implements WorkHandler<T> {
//	private BaseConsume<T> consume;
//	private Disruptor<T> disruptor;
//	
//	public WorkerPoolHandler2(BaseConsume<T> consume, EventFactory<T> factory, ThreadFactory threadFactory, WaitStrategy waitStrategy, int bufferSize) {
//		super(new Disruptor<>(factory, bufferSize, threadFactory, ProducerType.SINGLE, waitStrategy));
//		this.consume = consume;
//		disruptor = new Disruptor<>(factory, bufferSize, threadFactory, ProducerType.SINGLE, waitStrategy);
//		
//		disruptor.handleEventsWithWorkerPool(handlers);
//		disruptor.start();
//	}
//	
//	@Override
//	public void onEvent(T event) throws Exception {
//		
//	}
//	
//	
//	public boolean shutdown(Duration duration) {
//    	duration = duration == null ? Duration.ofSeconds(30) : duration;
//    	try {
//			disruptor.shutdown(duration.getSeconds(), TimeUnit.SECONDS);
//			return true;
//		} catch (TimeoutException e) {
//			return false;
//		}
//    }
//	
//}
