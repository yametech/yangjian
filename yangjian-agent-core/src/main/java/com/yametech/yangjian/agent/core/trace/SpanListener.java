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

import java.util.concurrent.atomic.AtomicLong;

import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.common.BaseEventListener;
import com.yametech.yangjian.agent.core.report.ReportManage;
import com.yametech.yangjian.agent.util.eventbus.consume.BaseConsume;

import zipkin2.Span;

/**
 * @author liuzhao
 * @Description
 * @date 2019年10月11日 下午4:51:45
 */
public class SpanListener extends BaseEventListener<Span> implements BaseConsume<Span> {
	private static final ILogger log = LoggerFactory.getLogger(SpanListener.class);
    private AtomicLong totalNum = new AtomicLong(0);// 总消费量
	private AtomicLong periodTotalNum = new AtomicLong(0);// 最近一个输出周期产生的事件量
	private IReportData report = ReportManage.getReport("SpanListener");
    
	public SpanListener() {
		super(Constants.ProductConsume.TRACE, "trace.consume.threadNum", ReportManage.getReport("SpanListener"));
	}
	
    @Override
    public BaseConsume<Span> getConsume() {
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
	public boolean test(Span t) {
		totalNum.getAndIncrement();
		periodTotalNum.getAndIncrement();
		return true;
	}
    
    @Override
	public void accept(Span t) {
    	if(!report.report(t)) {// TODO 需确认此处多线程消费是否有问题
    		log.warn("span上报失败：{}", t);
    	}
	}

	@Override
	protected int eventHashCode(Span event) {
		return event.traceId().hashCode();
	}
}
