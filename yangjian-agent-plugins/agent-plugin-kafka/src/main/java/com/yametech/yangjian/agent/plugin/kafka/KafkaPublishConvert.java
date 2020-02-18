/**
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

package com.yametech.yangjian.agent.plugin.kafka;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerRecord;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.convert.IMethodConvert;
import com.yametech.yangjian.agent.plugin.kafka.bean.MqInfo;
import com.yametech.yangjian.agent.plugin.kafka.context.ContextConstants;

/**
 * 输出kafka Qps数量
 *
 * @author liuzhao
 * @Description
 * @date 2019年11月6日 下午8:07:04
 */
public class KafkaPublishConvert implements IMethodConvert {
	
	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        if (!(thisObj instanceof IContext) || allArguments[0] == null) {
            return null;
        }
        MqInfo mqInfo = (MqInfo) ((IContext) thisObj)._getAgentContext(ContextConstants.KAFKA_CONTEXT_KEY);
        if (mqInfo == null) {
            return null;
        }
        ProducerRecord<?, ?> record = (ProducerRecord<?, ?>) allArguments[0];
        TimeEvent event = get(startTime);
		event.setIdentify(record.topic());
		return Arrays.asList(event);
    }
}
