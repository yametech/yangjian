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
package com.yametech.yangjian.agent.core.eventsubscribe.eventbus;

import java.util.Arrays;
import java.util.List;

import com.yametech.yangjian.agent.api.common.InstanceManage;
import com.yametech.yangjian.agent.core.common.BaseEventPublish;
import com.yametech.yangjian.agent.core.common.EventBusType;
import com.yametech.yangjian.agent.util.eventbus.consume.ConsumeConfig;

/**
 * 
 * @Description 
 * 
 * @author liuzhao
 * @date 2020年4月3日 下午4:52:21
 */
public class SubscribeEventBus extends BaseEventPublish<EventBean> {
    
    public SubscribeEventBus() {
		super(EventBusType.SUBCRIBE_EVENT);
	}
    
    @Override
	public List<ConsumeConfig<EventBean>> consumes() {
    	EventListener listener = new EventListener();
    	InstanceManage.registryInit(listener);
    	return Arrays.asList(listener);
	}
    
}
