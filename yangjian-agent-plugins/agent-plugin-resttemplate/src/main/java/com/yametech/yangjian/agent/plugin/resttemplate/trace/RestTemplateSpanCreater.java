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
package com.yametech.yangjian.agent.plugin.resttemplate.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.ExtraFieldPropagation;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MicrosClock;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import org.springframework.http.HttpMethod;

import java.lang.reflect.Method;
import java.net.URI;

/**
 * @author dengliming
 * @date 2020/6/26
 */
public class RestTemplateSpanCreater implements ISpanCreater<SpanInfo> {
    private MicrosClock MICROS_CLOCK;
    protected Tracer tracer;
    private ISpanSample spanSample;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.MICROS_CLOCK = new MicrosClock();
        this.tracer = tracing.tracer();
        this.spanSample = spanSample;
    }

    @Override
    public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        if (!spanSample.sample()) {
            return null;
        }
        long startTime = MICROS_CLOCK.nowMicros();
        if (startTime == -1L) {
            return null;
        }
        final URI requestURL = (URI) allArguments[0];
        final HttpMethod httpMethod = (HttpMethod) allArguments[1];
        Span span = tracer.nextSpan()
                .kind(Span.Kind.CLIENT)
                .name(requestURL.toString())
                .tag(Constants.Tags.COMPONENT, Constants.Component.SPRING_RESTTEMPLATE)
                .tag(Constants.Tags.HTTP_METHOD, httpMethod.name())
                .tag(Constants.Tags.PEER, requestURL.getHost() + ":" + requestURL.getPort())
                .start(startTime);
        // 自定义字段为了后续服务拓扑图生成
        ExtraFieldPropagation.set(span.context(), Constants.ExtraHeaderKey.REFERER_SERVICE, Constants.serviceName());
        span.remoteIpAndPort(requestURL.getHost(), requestURL.getPort());
        return new BeforeResult<>(null, new SpanInfo(span, tracer.withSpanInScope(span)), null);
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, Object ret, Throwable t,
                        BeforeResult<SpanInfo> beforeResult) {
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
