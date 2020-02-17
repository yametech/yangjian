package cn.ecpark.tool.util.eventbus.reactor.processor;


import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ecpark.tool.util.eventbus.reactor.assignor.MultiThreadAssignor;
import cn.ecpark.tool.util.eventbus.reactor.assignor.PollingAssignor;
import cn.ecpark.tool.util.eventbus.reactor.consume.BaseConsume;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.TopicProcessor;

/**
 * 异步消费数据，可开启多线程消费，可添加多个消费者
 * @param <T>
 */
public class TopicEventBus<T> extends BaseEventBus<T> {
    private Logger log = LoggerFactory.getLogger(EmitterEventBus.class);
    private TopicProcessor<T> processor;

    TopicEventBus(TopicProcessor<T> processor, String name) {
        super(processor, name);
        this.processor = processor;
    }

    /**
     * 添加订阅者，可添加多个监听者
     */
    @Override
    public void addSubscriber(BaseConsume<T> consume) {
        final int threadNum = getThreadNum(consume);
        for(int i = 0; i < threadNum; i++) {
            final int num = i;
            subscribeFlux.filter(msg -> {
                    MultiThreadAssignor<T> assignor = consume.assignor();
                    if(assignor == null && threadNum > 1) {
                        assignor = new PollingAssignor<>();
                    }
                    try {// 此处增加异常捕获防止异常后停止消费
                        return consume.test(msg) &&
                                (threadNum == 1 || assignor.threadNum(msg, threadNum) == num);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        log.error("filter error: class={}，event={}，error=", msg.getClass(), msg, e);
                        return false;
                    }
                })
//	            .buffer(3)
	            .onBackpressureBuffer(1, BufferOverflowStrategy.DROP_OLDEST).onBackpressureDrop((t) -> {
	            	log.warn("[{}]-BackpressureDrop:{}", "test123", t);
	            })
                .subscribe(msg -> {
                    try {// 此处增加异常捕获防止异常后停止消费
                        consume.accept(msg);
                    } catch (Throwable e) {
                        log.error("subscribe error: class={}，event={}，error=", msg.getClass(), msg, e);
                    }
                });
        }
        log.info("{}已开启{}个线程消费", consume.name(), threadNum);
    }

    private int getThreadNum(BaseConsume<T> consume) {
        int threadNum = consume.parallelism();
        if(threadNum < 1) {
            threadNum = 1;
        } else if(threadNum > Runtime.getRuntime().availableProcessors() * 2) {
            threadNum = Runtime.getRuntime().availableProcessors() * 2;
        }
        return threadNum;
    }

    @Override
    public void publish(T msg) {
        processor.onNext(msg);
    }

    /**
     * 关闭当前eventBus实例
     */
    @Override
    public boolean shutdown(Duration duration) {
        boolean stopSuccess = processor.awaitAndShutdown(duration == null ? Duration.ofSeconds(10) : duration);
        if (!stopSuccess) {
            log.error("停止异步事件超时，可能导致数据丢失");
        }
        return stopSuccess;
    }
}
