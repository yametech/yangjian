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
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.TraceUtil;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.plugin.kafka.bean.MqInfo;
import com.yametech.yangjian.agent.plugin.kafka.context.ContextConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import java.lang.reflect.Method;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author dengliming
 * @date 2020/4/27
 */
public class KafkaConsumerSpanCreater implements ISpanCreater<Void> {

    protected Tracer tracer;
    private ISpanSample spanSample;
    private TraceContext.Extractor<Headers> extractor;
    private CurrentTraceContext currentTraceContext;
    private static final String SPAN_NAME_FORMAT = "Kafka/%s/poll";

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracer = tracing.tracer();
        this.spanSample = spanSample;
        this.extractor = tracing.propagation().extractor((carrier, key) -> {
            Header header = carrier.lastHeader(key);
            if (header == null || header.value() == null) {
                return null;
            }
            return new String(header.value(), UTF_8);
        });
        this.currentTraceContext = tracing.currentTraceContext();
    }

    @Override
    public BeforeResult<Void> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, Object ret, Throwable t, BeforeResult<Void> beforeResult) {
        if (ret == null) {
            return null;
        }

        MqInfo mqInfo = (MqInfo) ((IContext) thisObj)._getAgentContext(ContextConstants.KAFKA_CONTEXT_KEY);
        if (mqInfo == null) {
            return ret;
        }
        ConsumerRecords<?, ?> records = (ConsumerRecords<?, ?>) ret;
        if (records == null || records.isEmpty()) {
            return ret;
        }
        TraceContextOrSamplingFlags context = null;
        for (ConsumerRecord<?, ?> record : records) {
            if (!spanSample.sample()) {
                return null;
            }
            context = extractor.extract(record.headers());
            tracer.nextSpan(context)
                    .kind(Span.Kind.CONSUMER)
                    .name(String.format(SPAN_NAME_FORMAT, record.topic()))
                    .tag(Constants.Tags.MQ_TOPIC, record.topic())
                    .tag(Constants.Tags.MQ_SERVER, mqInfo.getIpPorts())
                    .tag(Constants.Tags.MQ_CONSUMER, mqInfo.getConsumeGroup())
                    .start(TraceUtil.nowMicros())
                    .finish();
        }
        // 此处为了将context传递到当前线程、连接后面在该线程的链路
        currentTraceContext.maybeScope(context == null ? null : context.context());
        return ret;
    }
}
