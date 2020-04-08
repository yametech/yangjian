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
package com.yametech.yangjian.agent.core.trace.base;

import java.util.ArrayList;
import java.util.List;

import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.core.common.BaseEventPublish;
import com.yametech.yangjian.agent.core.core.InstanceManage;
import com.yametech.yangjian.agent.core.report.ReportManage;
import com.yametech.yangjian.agent.core.trace.SpanListener;
import com.yametech.yangjian.agent.util.eventbus.consume.ConsumeConfig;

/**
 * 
 * @Description 
 * 
 * @author liuzhao
 * @date 2020年4月3日 下午4:52:21
 */
public class TraceEventBus extends BaseEventPublish<TraceSpan> {
    
    public TraceEventBus() {
		super(Constants.ProductConsume.TRACE, "trace", ReportManage.getReport("TraceEventBus"));
	}
    
    @Override
	public List<ConsumeConfig<TraceSpan>> consumes() {
		List<ConsumeConfig<TraceSpan>> consumes = new ArrayList<>();
		consumes.add(InstanceManage.getSpiInstance(SpanListener.class));
		return consumes;
	}
    
}
