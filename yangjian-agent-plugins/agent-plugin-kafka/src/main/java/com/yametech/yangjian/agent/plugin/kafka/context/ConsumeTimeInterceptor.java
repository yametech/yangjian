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

package com.yametech.yangjian.agent.plugin.kafka.context;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.interceptor.IMethodAOP;
import com.yametech.yangjian.agent.plugin.kafka.bean.KafkaConsumeRT;

/**
 * 增强类定义
 *
 * @author liuzhao
 * @Description
 * @date 2019年11月8日 下午6:13:04
 */
public class ConsumeTimeInterceptor implements IMethodAOP<Object> {

    @Override
    public BeforeResult<Object> before(Object thisObj, Object[] allArguments, Method method) {
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult<Object> beforeResult,
                        Object ret, Throwable t, Map<Class<?>, Object> globalVar) {
        if (!(thisObj instanceof IContext)) {
            return ret;
        }
        IContext context = (IContext) thisObj;
        KafkaConsumeRT consumeRT = (KafkaConsumeRT) context._getAgentContext(ContextConstants.KAFKA_CONSUME_INFO);
        ConsumerRecords<?, ?> records = (ConsumerRecords<?, ?>) ret;
        if (consumeRT == null) {
            consumeRT = new KafkaConsumeRT();
            context._setAgentContext(ContextConstants.KAFKA_CONSUME_INFO, consumeRT);
        }
        consumeRT.setTopicNum(getTopicNum(records));
        consumeRT.setStartTime(System.currentTimeMillis());
        return ret;
    }

    private Map<String, Integer> getTopicNum(ConsumerRecords<?, ?> records) {
        if (records == null) {
            return null;
        }
        Map<String, Integer> topicNum = new HashMap<>();
        for (ConsumerRecord<?, ?> record : records) {
            Integer num = topicNum.getOrDefault(record.topic(), 0);
            topicNum.put(record.topic(), num + 1);
        }
        return topicNum;
    }

}
