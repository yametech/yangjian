package cn.ecpark.tool.util.eventbus.reactor.processor;

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
