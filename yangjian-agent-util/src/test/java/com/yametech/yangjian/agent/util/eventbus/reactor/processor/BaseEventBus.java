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
package com.yametech.yangjian.agent.util.eventbus.reactor.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;


public abstract class BaseEventBus<T> implements EventBus<T> {
    private static Logger log = LoggerFactory.getLogger(BaseEventBus.class);
    protected Flux<T> subscribeFlux;

    protected BaseEventBus(Flux<T> subscribeFlux, String name) {
    	name = name == null ? "" : name;
    	subscribe(subscribeFlux, name);
    }
    
    private void subscribe(Flux<T> subscribeFlux, String name) {
    	this.subscribeFlux = subscribeFlux.onBackpressureBuffer(1, BufferOverflowStrategy.DROP_OLDEST).onBackpressureDrop((t) -> {
        	log.warn("[{}]-BackpressureDrop:{}", name, t);
        }).doFinally((t) -> {
            log.info("[{}]-Finally:{}", name, t.name());
        }).doOnError((e) -> {
            log.warn("[{}]-Error", name, e);
        }).doOnCancel(()->{
            log.info("[{}]-Canal", name);
        }).doOnTerminate(()->{
            log.info("[{}]-Terminate", name);
        }).doAfterTerminate(()->{
            log.info("[{}]-AfterTerminate", name);
        });
    }
}
