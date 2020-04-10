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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.ExceptionHandler;
import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
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
public abstract class BaseEventPublish<T> implements IAppStatusListener, ISchedule, IConfigReader {
    private static final ILogger log = LoggerFactory.getLogger(BaseEventPublish.class);
    private static final int MIN_BUFFER_SIZE = 1 << 6;
    private static final String BUFFER_SIZE_KEY_PREFIX = "bufferSize.";
    private static final String INTERVAL_KEY_PREFIX = "publishMetricOutput.interval.";
    private IReportData report;
    private EventBus<T> eventBus;
    private String metricType;
    private String configKeySuffix;
    private int bufferSize = MIN_BUFFER_SIZE;
    private int interval = 5;
    
    public BaseEventPublish(String metricType, String configKeySuffix, IReportData report) {
    	this.metricType = metricType;
    	this.configKeySuffix = configKeySuffix;
		this.report = report;
	}
    
    @Override
    public Set<String> configKey() {
    	return new HashSet<>(Arrays.asList(BUFFER_SIZE_KEY_PREFIX + configKeySuffix, INTERVAL_KEY_PREFIX + configKeySuffix));
    }
    
    @Override
    public void configKeyValue(Map<String, String> kv) {
    	if (kv == null) {
            return;
        }
    	String bufferSizeStr = kv.get(BUFFER_SIZE_KEY_PREFIX + configKeySuffix);
    	if(bufferSizeStr != null) {
    		try {
            	int bufferSizeConfig = Integer.parseInt(bufferSizeStr);
            	if(bufferSizeConfig > MIN_BUFFER_SIZE) {
            		this.bufferSize = bufferSizeConfig;
            	}
            } catch(Exception e) {
            	log.warn("{}配置错误：{}", BUFFER_SIZE_KEY_PREFIX + configKeySuffix, bufferSizeStr);
            }
    	}
    	
    	String intervalStr = kv.get(INTERVAL_KEY_PREFIX + configKeySuffix);
    	if(intervalStr != null) {
    		try {
    			interval = Integer.parseInt(intervalStr);
            } catch(Exception e) {
            	log.warn("{}配置错误：{}", INTERVAL_KEY_PREFIX + configKeySuffix, intervalStr);
            }
    	}
    }
    
    protected abstract List<ConsumeConfig<T>> consumes();
    
    @Override
    public void beforeRun() {
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
        log.info("{} eventPublish inited.", metricType);
    }

    public void publish(Consumer<T> consumer) {
        eventBus.publish(event -> {
        	incrTotalNum();
        	if (event == null) {
            	incrDiscardNum();
                return;
            }
        	consumer.accept(event);
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
        return interval;
    }

    private AtomicLong discardNum = new AtomicLong(0);// 总共丢弃的数据量
    private AtomicLong periodDiscardNum = new AtomicLong(0);// 最近一个输出周期丢弃的数据量
    private AtomicLong totalNum = new AtomicLong(0);// 总产生的事件量
    private AtomicLong periodTotalNum = new AtomicLong(0);// 最近一个输出周期产生的事件量

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
