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

package com.yametech.yangjian.agent.util.eventbus;


import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yametech.yangjian.agent.util.eventbus.reactor.assignor.HashAssignor;
import com.yametech.yangjian.agent.util.eventbus.reactor.assignor.MultiThreadAssignor;
import com.yametech.yangjian.agent.util.eventbus.reactor.consume.BaseConsume;
import com.yametech.yangjian.agent.util.eventbus.reactor.processor.EventBus;
import com.yametech.yangjian.agent.util.eventbus.reactor.processor.EventBusBuilder;

/**
 * eventBus使用示例
 */
public class EventBusTest {
    private static Logger log  = LoggerFactory.getLogger(EventBusTest.class);

    /**
     * 测试同步生产消费
     */
    @Test
    public void testSync() {
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4,
//                30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
        EventBus<String> eventBus = EventBusBuilder.asyncConsume(false)
//                .executor(executor)
                .bufferSize(1)
                .parallelPublish(true)
                .build();

        String[] consumerGroup = new String[] {"flux1", "flux2", "flux3"};
        for(String group : consumerGroup) {
            eventBus.addSubscriber(new BaseConsume<String>() {
                @Override
                public void accept(String t) {
                    try {Thread.sleep(1500);} catch (InterruptedException ex) {ex.printStackTrace();}
                    log.info(group + " consume>>>>" + t);
                }
                @Override
                public boolean test(String t) {
                    return t.startsWith(group);
                }
                @Override
                public String name() {
                	return group;
                }
            });
        }
        IntStream.rangeClosed(1, 10).parallel().forEach(e -> {
            for(String group : consumerGroup) {
                log.info("publish:" + group + "-" + e);
                eventBus.publish(group + "-" + e);
                log.info("done:" + group + "-" + e);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        });
        System.err.println("关闭：" + eventBus.shutdown(Duration.ofSeconds(20)));
    }

    /**
     * 异步生产消费，测试包含：
     * 	消息分发；
     * 	多线程消费；
     * 	
     * 
     */
    @Test
    public void testAsync() {
    	ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 8, 30, TimeUnit.SECONDS, 
    			new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
        EventBus<String> eventBus = EventBusBuilder.asyncConsume(true)
        		.name("testXXX")
        		.executor(executor)
                .bufferSize(2 << 3)
                .parallelPublish(true)
                .build();

        String[] consumerGroup = new String[] {"flux1", "flux2", "flux3"};
        for(String group : consumerGroup) {
            eventBus.addSubscriber(new BaseConsume<String>() {
                @Override
                public int parallelism() {
                    return 1;// 同时开启两个线程消费
                }
                @Override
                public MultiThreadAssignor<String> assignor() {
                    return new HashAssignor<>();// 使用hash策略，业务放需重写其中的hash，保证一类数据的顺序性
                }

                @Override
                public void accept(String t) {// 消费数据
                    try {Thread.sleep(3000);} catch (InterruptedException ex) {ex.printStackTrace();}
                    log.info(group + " consume:" + t);
                }
                @Override
                public boolean test(String t) {// 判断当前数据是否需要消费
                    return t.startsWith(group);
                }
                @Override
                public String name() {
                	return group;
                }
            });
        }
        IntStream.rangeClosed(1, 10).forEach(e -> {
            for(String group : consumerGroup) {
                eventBus.publish(group + "-" + e);
                log.info("publish:" + group + "-" + e);
            }
        });
        System.err.println("关闭：" + eventBus.shutdown(Duration.ofSeconds(20)));
    }

}
