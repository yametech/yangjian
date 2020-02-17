package cn.ecpark.tool.util.eventbus.reactor.processor;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ecpark.tool.util.eventbus.reactor.consume.BaseConsume;
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
