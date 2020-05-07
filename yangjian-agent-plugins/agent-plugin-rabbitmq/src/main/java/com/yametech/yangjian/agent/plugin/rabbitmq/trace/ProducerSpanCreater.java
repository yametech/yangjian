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
package com.yametech.yangjian.agent.plugin.rabbitmq.trace;

import brave.Span;
import brave.Tracing;
import brave.propagation.TraceContext;
import com.rabbitmq.client.AMQP;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.TraceUtil;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import com.yametech.yangjian.agent.plugin.rabbitmq.bean.MqInfo;
import com.yametech.yangjian.agent.plugin.rabbitmq.context.ContextConstants;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dengliming
 * @date 2020/4/30
 */
public class ProducerSpanCreater extends AbstractSpanCreater {

    private TraceContext.Injector<Map<String, Object>> injector;
    private static final String SPAN_NAME = "RabbitMQ/producer";

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        super.init(tracing, spanSample);
        this.injector = tracing.propagation().injector((carrier, key, value) -> carrier.put(key, value));
    }

    @Override
    public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        if (!(thisObj instanceof IContext)) {
            return null;
        }
        MqInfo mqInfo = (MqInfo) ((IContext) thisObj)._getAgentContext(ContextConstants.RABBITMQ_CONTEXT_KEY);
        if (mqInfo == null) {
            return null;
        }
        if (!spanSample.sample()) {
            return null;
        }
        AMQP.BasicProperties properties = (AMQP.BasicProperties) allArguments[4];
        AMQP.BasicProperties.Builder propertiesBuilder;

        Map<String, Object> headers = new HashMap<>();
        if (properties != null) {
            propertiesBuilder = properties.builder()
                    .appId(properties.getAppId())
                    .clusterId(properties.getClusterId())
                    .contentEncoding(properties.getContentEncoding())
                    .contentType(properties.getContentType())
                    .correlationId(properties.getCorrelationId())
                    .deliveryMode(properties.getDeliveryMode())
                    .expiration(properties.getExpiration())
                    .messageId(properties.getMessageId())
                    .priority(properties.getPriority())
                    .replyTo(properties.getReplyTo())
                    .timestamp(properties.getTimestamp())
                    .type(properties.getType())
                    .userId(properties.getUserId());
            if (properties.getHeaders() != null) {
                headers.putAll(properties.getHeaders());
            }
        } else {
            propertiesBuilder = new AMQP.BasicProperties.Builder();
        }

        String exChangeName = (String) allArguments[0];
        String queueName = (String) allArguments[1];
        Span span = tracer.nextSpan()
                .kind(Span.Kind.CONSUMER)
                .name(SPAN_NAME)
                .tag(Constants.Tags.MQ_TOPIC, exChangeName)
                .tag(Constants.Tags.MQ_QUEUE, queueName)
                .tag(Constants.Tags.MQ_SERVER, mqInfo.getIpPorts())
                .start(TraceUtil.nowMicros());

        injector.inject(span.context(), headers);
        // 加上这一步主要是因为原来这个参数可能为空所以重新赋值
        allArguments[4] = propertiesBuilder.headers(headers).build();
        return new BeforeResult<>(null, new SpanInfo(span, tracer.withSpanInScope(span)), null);
    }
}
