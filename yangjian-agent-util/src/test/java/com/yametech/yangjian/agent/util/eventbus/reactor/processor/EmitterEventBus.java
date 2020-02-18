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

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yametech.yangjian.agent.util.eventbus.reactor.consume.BaseConsume;
import reactor.core.publisher.EmitterProcessor;

public class EmitterEventBus<T> implements EventBus<T> {
    private Logger log = LoggerFactory.getLogger(EmitterEventBus.class);
    private EmitterProcessor<T> processor;

    EmitterEventBus(EmitterProcessor<T> processor) {
        this.processor = processor;
    }

    @Override
    public void addSubscriber(BaseConsume<T> consume) {
        processor.filter(msg -> {
            try {// 此处增加异常捕获防止异常后停止消费
                return consume.test(msg);
            } catch (Throwable e) {
                log.error("filter error: class={}，event={}，error=", msg.getClass(), msg, e);
                return false;
            }
        }).subscribe(msg -> {
            try {// 此处增加异常捕获防止异常后停止消费
                consume.accept(msg);
            } catch (Throwable e) {
                log.error("subscribe error: class={}，event={}，error=", msg.getClass(), msg, e);
            }
        });
        log.info("{}已开启消费", consume.getClass());
    }

    @Override
    public void publish(T msg) {
        processor.onNext(msg);
    }

    @Override
    public boolean shutdown(Duration duration) {
        try {
            processor.blockLast(duration);
            return true;
        } catch(Exception e) {
            log.error("停止EmitterProcessor异常", e);
            return false;
        }
    }
}
