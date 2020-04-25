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
package com.yametech.yangjian.agent.core.metric.base;

import java.util.ArrayList;
import java.util.List;

import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.core.common.BaseEventPublish;
import com.yametech.yangjian.agent.core.common.ConfigSuffix;
import com.yametech.yangjian.agent.core.core.InstanceManage;
import com.yametech.yangjian.agent.core.metric.consume.RTEventListener;
import com.yametech.yangjian.agent.util.eventbus.consume.ConsumeConfig;

/**
 * @author dengliming
 * @date 2019/12/12
 */
public class MetricEventBus extends BaseEventPublish<ConvertTimeEvent> {
    
    public MetricEventBus() {
		super(Constants.ProductConsume.METRIC, ConfigSuffix.METRIC);
	}
    
    @Override
	public List<ConsumeConfig<ConvertTimeEvent>> consumes() {
		List<ConsumeConfig<ConvertTimeEvent>> consumes = new ArrayList<>();
		RTEventListener listener = new RTEventListener();
    	InstanceManage.registryInit(listener);
		consumes.add(listener);
		return consumes;
	}
    
}
