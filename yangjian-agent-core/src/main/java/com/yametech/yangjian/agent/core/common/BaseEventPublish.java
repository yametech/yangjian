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
package com.yametech.yangjian.agent.core.common;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.ExceptionHandler;
import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.config.Config;
import com.yametech.yangjian.agent.core.metric.MetricData;
import com.yametech.yangjian.agent.core.util.Util;
import com.yametech.yangjian.agent.util.CustomThreadFactory;
import com.yametech.yangjian.agent.util.eventbus.EventBusBuilder;
import com.yametech.yangjian.agent.util.eventbus.consume.ConsumeConfig;
import com.yametech.yangjian.agent.util.eventbus.process.EventBus;

/**
 * 事件缓存基类，附带metric
 * @Description 
 * 
 * @author liuzhao
 * @date 2020年4月3日 下午4:52:35 
 * @param <T>
 */
public abstract class BaseEventPublish<T> implements IAppStatusListener, ISchedule {
    private static final ILogger log = LoggerFactory.getLogger(BaseEventPublish.class);
    private static final int MIN_BUFFER_SIZE = 1 << 6;
    private IReportData report;
    private EventBus<T> eventBus;
    private String metricType;
    
    public BaseEventPublish(String metricType, IReportData report) {
    	this.metricType = metricType;
		this.report = report;
	}
    
    protected abstract List<ConsumeConfig<T>> consumes();
    
    @Override
    public void beforeRun() {
        Integer bufferSize = Config.CALL_EVENT_BUFFER_SIZE.getValue();
        if (bufferSize == null || bufferSize < MIN_BUFFER_SIZE) {
            bufferSize = MIN_BUFFER_SIZE;
        }
        @SuppressWarnings("unchecked")
		Class<T> cls = (Class<T>) Util.superClassGeneric(this.getClass(), 0);
        eventBus = EventBusBuilder
                .create(this::consumes)
                .parallelPublish(true)
                .executor(new CustomThreadFactory(metricType, true))
                .bufferSize(bufferSize)
                .waitStrategy(new BlockingWaitStrategy())
//				.waitStrategy(new YieldingWaitStrategy())
                .setDiscardFull(true)
                .build(cls, new ExceptionHandler<T>() {
                    @Override
                    public void handleEventException(Throwable e, long sequence, T callEvent) {
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
        log.info("{} inited.", metricType);
    }

    public void publish(Consumer<T> consumer) {
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

    private static AtomicLong discardNum = new AtomicLong(0);// 总共丢弃的数据量
    private static AtomicLong periodDiscardNum = new AtomicLong(0);// 最近一个输出周期丢弃的数据量
    private static AtomicLong totalNum = new AtomicLong(0);// 总产生的事件量
    private static AtomicLong periodTotalNum = new AtomicLong(0);// 最近一个输出周期产生的事件量

    @Override
    public void execute() {
        long periodTotal = periodTotalNum.getAndSet(0);
        long periodDiscard = periodDiscardNum.getAndSet(0);
        Map<String, Object> params = new HashMap<>();
        params.put("total_num", totalNum.get());
        params.put("period_seconds", interval());
        params.put("period_num", periodTotal);
        params.put("total_discard_num", discardNum.get());
        params.put("period_discard_num", periodDiscard);
        MetricData metricData = MetricData.get(null, "product/" + metricType, params);
        if(!report.report(metricData)) {
        	log.warn("上报失败：{}", metricData);
        }
    }
}
