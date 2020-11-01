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
package com.yametech.yangjian.agent.core.trace;

import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.common.MultiReportFactory;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.common.BaseEventListener;
import com.yametech.yangjian.agent.core.common.EventBusType;
import com.yametech.yangjian.agent.core.trace.base.TraceSpan;
import com.yametech.yangjian.agent.util.eventbus.consume.BaseConsume;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author liuzhao
 * @Description
 * @date 2019年10月11日 下午4:51:45
 */
public class SpanListener extends BaseEventListener<TraceSpan> implements BaseConsume<TraceSpan> {
	private static final ILogger log = LoggerFactory.getLogger(SpanListener.class);
    private AtomicLong totalNum = new AtomicLong(0);// 总消费量
	private AtomicLong periodTotalNum = new AtomicLong(0);// 最近一个输出周期产生的事件量
	private IReportData report = MultiReportFactory.getReport("spanListener");
    
	public SpanListener() {
		super(EventBusType.TRACE);
	}
	
    @Override
    public BaseConsume<TraceSpan> getConsume() {
        // 该方法会调用parallelism次，如果返回同一个实例且parallelism>0，则实例为多线程消费
        return this;
    }
    
    @Override
    protected long getTotalNum() {
        return totalNum.get();
    }

    @Override
    protected long getPeriodNum() {
        return periodTotalNum.getAndSet(0);
    }

	@Override
	public boolean test(TraceSpan t) {
		totalNum.getAndIncrement();
		periodTotalNum.getAndIncrement();
		return true;
	}
    
    @Override
	public void accept(TraceSpan t) {
    	if(!report.report("[" + t.getSpan() + "]")) {// 兼容zipkin数据上报格式，方便测试
    		log.warn("span report failed: {}", t.getSpan());
    	}
	}

//	@Override
//	protected int eventHashCode(TraceSpan event) {
//		return event.getSpan().traceId().hashCode();
//	}
}
