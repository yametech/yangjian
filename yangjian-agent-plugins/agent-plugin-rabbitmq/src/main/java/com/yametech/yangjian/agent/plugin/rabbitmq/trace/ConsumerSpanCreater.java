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
import brave.propagation.TraceContextOrSamplingFlags;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import com.yametech.yangjian.agent.plugin.rabbitmq.bean.MqInfo;
import com.yametech.yangjian.agent.plugin.rabbitmq.context.ContextConstants;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author dengliming
 * @date 2020/4/30
 */
public class ConsumerSpanCreater extends AbstractSpanCreater {
    private TraceContext.Extractor<Map<String, Object>> extractor;
    private static final String SPAN_NAME = "RabbitMQ/Consumer";

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        super.init(tracing, spanSample);
        this.extractor = tracing.propagation().extractor((carrier, key) -> {
            if (carrier.containsKey(key)) {
                return carrier.get(key).toString();
            }
            return null;
        });
    }

    @Override
    public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        if (!spanSample.sample()) {
            return null;
        }
        if (!(thisObj instanceof IContext)) {
            return null;
        }
        MqInfo mqInfo = (MqInfo) ((IContext) thisObj)._getAgentContext(ContextConstants.RABBITMQ_CONTEXT_KEY);
        if (mqInfo == null) {
            return null;
        }
        long startTime = MICROS_CLOCK.nowMicros();
        if (startTime == -1L) {
            return null;
        }
        Envelope envelope = (Envelope) allArguments[1];
        AMQP.BasicProperties properties = (AMQP.BasicProperties) allArguments[2];
        TraceContextOrSamplingFlags traceContextOrSamplingFlags = extractor.extract(properties.getHeaders());
        Span span = traceContextOrSamplingFlags != null ? tracer.nextSpan(traceContextOrSamplingFlags) : tracer.nextSpan();
        span.kind(Span.Kind.CONSUMER)
                .name(SPAN_NAME)
                .tag(Constants.Tags.COMPONENT, Constants.Component.RABBITMQ)
                .tag(Constants.Tags.PEER, mqInfo.getIpPorts())
                .tag(Constants.Tags.MQ_TOPIC, envelope.getExchange())
                .tag(Constants.Tags.MQ_QUEUE, envelope.getRoutingKey())
                .start(startTime);
        return new BeforeResult<>(null, new SpanInfo(span, tracer.withSpanInScope(span)), null);
    }
}
