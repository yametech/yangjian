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
import brave.propagation.TraceContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;

import java.lang.reflect.Method;

/**
 * @author dengliming
 * @date 2020/6/26
 */
public class CreateRequestInterceptor implements ISpanCreater<SpanInfo> {

    protected Tracer tracer;

    private TraceContext.Injector<HttpHeaders> injector;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracer = tracing.tracer();
        this.injector = tracing.propagation().injector(HttpHeaders::set);
    }

    @Override
    public BeforeResult before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, Object ret, Throwable t,
                        BeforeResult<SpanInfo> beforeResult) {
        Span span = tracer.currentSpan();
        if (span == null) {
            return ret;
        }
        ClientHttpRequest clientHttpRequest = (ClientHttpRequest) ret;
        if (clientHttpRequest instanceof AbstractClientHttpRequest) {
            AbstractClientHttpRequest httpRequest = (AbstractClientHttpRequest) clientHttpRequest;
            injector.inject(span.context(), httpRequest.getHeaders());
        }
        return ret;
    }
}
