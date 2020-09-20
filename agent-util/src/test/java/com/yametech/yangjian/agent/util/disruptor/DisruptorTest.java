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
package com.yametech.yangjian.agent.util.disruptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import com.yametech.yangjian.agent.util.eventbus.EventBusBuilder;
import com.yametech.yangjian.agent.util.eventbus.assignor.HashAssignor;
import com.yametech.yangjian.agent.util.eventbus.assignor.MultiThreadAssignor;
import com.yametech.yangjian.agent.util.eventbus.consume.BaseConfigConsume;
import com.yametech.yangjian.agent.util.eventbus.process.EventBus;
import com.yametech.yangjian.agent.util.eventbus.regist.IConsumeRegist;

public class DisruptorTest {
//	private static final Logger log = LoggerFactory.getLogger(DisruptorTest.class);
	
	@Test
	public void testUtil() throws InterruptedException {
		AtomicInteger consumeNum = new AtomicInteger(0);
		AtomicInteger discardNum = new AtomicInteger(0);
		EventBus<MethodEvent> eventBus = EventBusBuilder.create(new IConsumeRegist() {
				@Override
				public List<BaseConfigConsume<?>> regist() {
						List<BaseConfigConsume<?>> consumes = new ArrayList<>();
						consumes.add((MethodEvent event) -> {
//								try {
//									Thread.sleep(Math.abs(new Random().nextInt() % 10));
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
								System.err.println("1 consume:" + event + " - " + JSON.toJSONString(event));
						});
						consumes.add(new BaseConfigConsume<MethodEvent>() {
							@Override
							public void accept(MethodEvent event) {
								event.setEventTime(0L);
								event.setStartTime(0L);
								System.err.println("2 consume:" + event + " - " + JSON.toJSONString(event));
								consumeNum.getAndIncrement();
							}
							
							@Override
							public int parallelism() {
								return 2;
							}
						});
						consumes.add(new BaseConfigConsume<MethodEvent>() {
							@Override
							public void accept(MethodEvent event) {
								event.setEventTime(1L);
								event.setStartTime(1L);
								System.err.println("3 consume:" + event + " - " + JSON.toJSONString(event));
							}
							@Override
							public int parallelism() {
								return 2;
							}
							@Override
							public MultiThreadAssignor<MethodEvent> assignor() {
								return new HashAssignor<MethodEvent>() {
									@Override
									public int hashValue(MethodEvent msg) {
										return Objects.hashCode(msg.getEventTime());
									}
								};
							}
						});
						return consumes;
				}
			})
				.bufferSize(1 << 5)
				.parallelPublish(true)
//				.waitStrategy(new BlockingWaitStrategy())// 160
//				.waitStrategy(new BusySpinWaitStrategy())// 110
//				.waitStrategy(new LiteBlockingWaitStrategy())// 150
//				.waitStrategy(new SleepingWaitStrategy())// 120
				.waitStrategy(new YieldingWaitStrategy())// 120
//				.setDiscardFull(true)
//				.setDiscardConsumer((event) -> {discardNum.getAndIncrement();})
//				.setDiscardConsumer((event) -> log.info("discard:{}", event))
				.build(MethodEvent.class);
		long startMillis = System.currentTimeMillis();
		for (int i = 0; i < 3; i++) {
			eventBus.publish((event) -> {
				event.setMethod(null);
				event.setArguments(null);
				event.setEventTime(System.currentTimeMillis());
				event.setStartTime(System.currentTimeMillis());
				event.setThrowable(null);
			});
//			System.err.println("publish " + i);
		}
		System.out.println("发布耗时：" + (System.currentTimeMillis() - startMillis));
		Thread.sleep(1000_000);
		eventBus.shutdown(null);
		System.out.println("消费耗时：" + (System.currentTimeMillis() - startMillis) + "，消费条数：" + consumeNum.get() + "，丢弃条数：" + discardNum.get() + "，处理总数：" + (consumeNum.get() + discardNum.get()));
	}
	
	@Test
	public void test() throws InterruptedException {
//		MethodEventFactory factory = new MethodEventFactory();
		int ringBufferSize = 1 << 2;
		System.err.println("ringBufferSize=" + ringBufferSize);
		// ProducerType要设置为MULTI，后面才可以使用多生产者模式
		Disruptor<MethodEvent> disruptor = new Disruptor<MethodEvent>(() -> new MethodEvent(), 
				ringBufferSize, Executors.defaultThreadFactory(), ProducerType.MULTI, 
				new BlockingWaitStrategy());
//				new YieldingWaitStrategy());
//				new TimeoutBlockingWaitStrategy(1, TimeUnit.NANOSECONDS));
		// 简化问题，设置为单消费者模式，也可以设置为多消费者及消费者间多重依赖。
		disruptor.handleEventsWith(new MethodEventHandler(), new MethodEventHandler2())
			.and(disruptor.handleEventsWithWorkerPool(new MethodEventHandler3(), new MethodEventHandler4()))
			.and(disruptor.handleEventsWithWorkerPool(new MethodEventHandler5(), new MethodEventHandler6()))
			.and(disruptor.handleEventsWith(new MethodEventHandler7(), new MethodEventHandler8()))
//			.handleEventsWithWorkerPool(new MethodEventHandler3(), new MethodEventHandler4())
//			.thenHandleEventsWithWorkerPool(new MethodEventHandler3(), new MethodEventHandler4())
			;
//		disruptor.setDefaultExceptionHandler(exceptionHandler);
		disruptor.start();
		EventProducer producer = new EventProducer(disruptor.getRingBuffer());
//		EventDiscardProducer producer = new EventDiscardProducer(disruptor.getRingBuffer(), true, (event) -> {});
//		EventDiscardProducer producer = new EventDiscardProducer(disruptor.getRingBuffer(), true, (event) -> System.err.println("discard:" + event));
		// 判断生产者是否已经生产完毕
//		final CountDownLatch countDownLatch = new CountDownLatch(3);
		// 单生产者，生产3条数据
		long startMillis = System.currentTimeMillis();
		for (int i = 0; i < 8; i++) {
			producer.onData(null, null, System.currentTimeMillis(), System.currentTimeMillis(), null);
			System.err.println("publish " + i);
//			Thread thread = new Thread() {
//				@Override
//				public void run() {
//					for (int i = 0; i < 3; i++) {
//						producer.onData(null, null, null, null, null);
//					}
//					countDownLatch.countDown();
//				}
//			};
//			thread.setName("producer thread " + l);
//			thread.start();
		}
//		countDownLatch.await();
		System.out.println("发布耗时：" + (System.currentTimeMillis() - startMillis));
		// 为了保证消费者线程已经启动，留足足够的时间。具体原因详见另一篇博客：disruptor的shutdown失效问题
		Thread.sleep(1000);
		disruptor.shutdown();
	}

}
