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
package com.yametech.yangjian.agent.plugin.httpclient.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.TraceUtil;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import org.apache.commons.httpclient.*;
import java.lang.reflect.Method;

/**
 * @author dengliming
 * @date 2020/4/25
 */
public class HttpClientSpanCreater implements ISpanCreater<SpanInfo> {

    protected Tracer tracer;
    private ISpanSample spanSample;
    private TraceContext.Injector<HttpMethod> injector;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracer = tracing.tracer();
        this.spanSample = spanSample;
        this.injector = tracing.propagation().injector(HttpMethod::addRequestHeader);
    }

    @Override
    public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        if (!spanSample.sample()) {
            return null;
        }
        final HttpMethod httpMethod = (HttpMethod) allArguments[1];
        final URI uri = httpMethod.getURI();
        Span span = tracer.nextSpan()
                .kind(Span.Kind.CLIENT)
                .name(getRequestURI(uri))
                .start(TraceUtil.nowMicros());
        span.tag(Constants.Tags.HTTP_METHOD, httpMethod.getName());
        span.tag(Constants.Tags.URL, uri.toString());
        span.remoteIpAndPort(httpMethod.getURI().getHost(), httpMethod.getURI().getPort());
        injector.inject(span.context(), httpMethod);
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

        if (ret != null) {
            span.getSpan().tag(Constants.Tags.STATUS_CODE, String.valueOf(ret));
        }

        span.getSpan().finish();
        if (span.getScope() != null) {
            span.getScope().close();
        }
        return ret;
    }

    private String getRequestURI(URI uri) throws URIException {
        String requestPath = uri.getPath();
        return requestPath != null && requestPath.length() > 0 ? requestPath : "/";
    }
}
