package cn.ecpark.tool.agent.core.aop.base;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.ExceptionHandler;

import cn.ecpark.tool.agent.api.IAppStatusListener;
import cn.ecpark.tool.agent.api.ISchedule;
import cn.ecpark.tool.agent.core.aop.consume.RTEventListener;
import cn.ecpark.tool.agent.core.config.Config;
import cn.ecpark.tool.agent.core.core.InstanceManage;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;
import cn.ecpark.tool.agent.core.util.CustomThreadFactory;
import cn.ecpark.tool.agent.core.util.LogUtil;
import cn.ecpark.tool.agent.util.eventbus.EventBusBuilder;
import cn.ecpark.tool.agent.util.eventbus.consume.ConsumeConfig;
import cn.ecpark.tool.agent.util.eventbus.process.EventBus;

/**
 * @author dengliming
 * @date 2019/12/12
 */
public class MetricEventBus implements IAppStatusListener, ISchedule {

    private static final ILogger log = LoggerFactory.getLogger(MetricEventBus.class);
    private static final int MIN_BUFFER_SIZE = 1 << 6;
    private EventBus<ConvertTimeEvent> eventBus;

    @Override
    public void beforeRun() {
        List<ConsumeConfig<?>> consumes = new ArrayList<>();
        consumes.add(InstanceManage.getSpiInstance(RTEventListener.class));
        Integer bufferSize = Config.CALL_EVENT_BUFFER_SIZE.getValue();
        if (bufferSize == null || bufferSize < MIN_BUFFER_SIZE) {
            bufferSize = MIN_BUFFER_SIZE;
        }
        eventBus = EventBusBuilder
                .create(() -> consumes)
                .parallelPublish(true)
                .executor(new CustomThreadFactory("callEvent", true))
                .bufferSize(bufferSize)
                .waitStrategy(new BlockingWaitStrategy())
//				.waitStrategy(new YieldingWaitStrategy())
                .setDiscardFull(true)
                .build(ConvertTimeEvent.class, new ExceptionHandler<ConvertTimeEvent>() {
                    @Override
                    public void handleEventException(Throwable e, long sequence, ConvertTimeEvent callEvent) {
                        log.error(e, "Disruptor handleEvent({}) error.", callEvent);
                    }

                    @Override
                    public void handleOnStartException(Throwable e) {
                        log.error(e, "Disruptor start error.");
                    }

                    @Override
                    public void handleOnShutdownException(Throwable e) {
                        log.error(e, "Disruptor shutdown error.");
                    }
                });
        log.info("MetricEventBus inited.");
    }

    public void publish(Consumer<ConvertTimeEvent> consumer) {
        eventBus.publish(event -> {
        	if (event == null) {
            	incrDiscardNum();
                return;
            }
        	consumer.accept(event);
            incrTotalNum();
        });
    }

    private void incrDiscardNum() {
        discardNum.getAndIncrement();
        periodDiscardNum.getAndIncrement();
    }

    private void incrTotalNum() {
        totalNum.getAndIncrement();
        periodTotalNum.getAndIncrement();
    }

    @Override
    public boolean shutdown(Duration duration) {
        long currentTotal = 0;
        while (currentTotal < totalNum.get()) {// 500毫秒内无调用事件则关闭，避免因关闭服务导致事件丢失
            try {
                currentTotal = totalNum.get();
                Thread.sleep(interval() * 1002L);
            } catch (InterruptedException e) {
                log.warn(e, "shutdown interrupted");
                Thread.currentThread().interrupt();
                break;
            }
        }
        duration = duration == null ? Duration.ofSeconds(10) : duration;
        if (eventBus != null) {
            return eventBus.shutdown(duration);
        }
        return true;
    }

    @Override
    public int interval() {
        return 2;
    }

    // TODO 修改类型提高并发
    private static AtomicLong discardNum = new AtomicLong(0);// 总共丢弃的数据量
    private static AtomicLong periodDiscardNum = new AtomicLong(0);// 最近一个输出周期丢弃的数据量
    private static AtomicLong totalNum = new AtomicLong(0);// 总产生的事件量
    private static AtomicLong periodTotalNum = new AtomicLong(0);// 最近一个输出周期产生的事件量

    @Override
    public void execute() {
        long periodTotal = periodTotalNum.getAndSet(0);
        long periodDiscard = periodDiscardNum.getAndSet(0);
        LogUtil.println("method-event/product", false,
                new AbstractMap.SimpleEntry<String, Object>("total_num", totalNum.get()),
                new AbstractMap.SimpleEntry<String, Object>("period_seconds", interval()), new AbstractMap.SimpleEntry<String, Object>("period_num", periodTotal),
                new AbstractMap.SimpleEntry<String, Object>("total_discard_num", discardNum.get()), new AbstractMap.SimpleEntry<String, Object>("period_discard_num", periodDiscard));
    }
}
