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
package com.yametech.yangjian.agent.plugin.kafka.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MicrosClock;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import com.yametech.yangjian.agent.plugin.kafka.bean.MqInfo;
import com.yametech.yangjian.agent.plugin.kafka.context.ContextConstants;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;

import java.lang.reflect.Method;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author dengliming
 * @date 2020/4/27
 */
public class KafkaProducerSpanCreater implements ISpanCreater<SpanInfo> {
    private static final MicrosClock MICROS_CLOCK = new MicrosClock();
    protected Tracer tracer;
    private ISpanSample spanSample;
    private TraceContext.Injector<Headers> injector;
    private static final String SPAN_NAME_FORMAT = "Kafka/%s/Producer";

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracer = tracing.tracer();
        this.spanSample = spanSample;
        this.injector = tracing.propagation().injector((carrier, key, value) -> {
            carrier.remove(key);
            carrier.add(key, value.getBytes(UTF_8));
        });
    }

    @Override
    public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        MqInfo mqInfo = (MqInfo) ((IContext) thisObj)._getAgentContext(ContextConstants.KAFKA_CONTEXT_KEY);
        if (mqInfo == null) {
            return null;
        }
        if (!spanSample.sample()) {
            return null;
        }
        long startTime = MICROS_CLOCK.nowMicros();
        if (startTime == -1L) {
            return null;
        }
        ProducerRecord<?, ?> record = (ProducerRecord<?, ?>) allArguments[0];
        Span span = tracer.nextSpan()
                .kind(Span.Kind.PRODUCER)
                .name(String.format(SPAN_NAME_FORMAT, record.topic()))
                .tag(Constants.Tags.COMPONENT, Constants.Component.KAFKA)
                .tag(Constants.Tags.PEER, mqInfo.getIpPorts())
                .tag(Constants.Tags.MQ_TOPIC, record.topic())
                .start(startTime);
        injector.inject(span.context(), record.headers());
        return new BeforeResult<>(null, new SpanInfo(span, tracer.withSpanInScope(span)), null);
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, Object ret, Throwable t, BeforeResult<SpanInfo> beforeResult) {
        if (beforeResult == null || beforeResult.getLocalVar() == null || beforeResult.getLocalVar().getSpan() == null) {
            return ret;
        }

        SpanInfo span = beforeResult.getLocalVar();
        if (t != null) {
            span.getSpan().error(t);
        }

        span.getSpan().finish();
        if (span.getScope() != null) {
            span.getScope().close();
        }
        return ret;
    }
}
