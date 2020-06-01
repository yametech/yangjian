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
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MicrosClock;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author dengliming
 * @date 2020/4/25
 */
public class CloseableHttpClientSpanCreater implements ISpanCreater<SpanInfo> {

    private static final MicrosClock MICROS_CLOCK = new MicrosClock();
    protected Tracer tracer;
    private ISpanSample spanSample;
    private TraceContext.Injector<HttpRequest> injector;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracer = tracing.tracer();
        this.spanSample = spanSample;
        this.injector = tracing.propagation().injector(HttpRequest::setHeader);
    }

    @Override
    public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        if (allArguments[0] == null || allArguments[1] == null) {
            return null;
        }
        if (!spanSample.sample()) {
            return null;
        }
        long startTime = MICROS_CLOCK.nowMicros();
        if (startTime == -1L) {
            return null;
        }
        final HttpHost httpHost = (HttpHost) allArguments[0];
        HttpRequest httpRequest = (HttpRequest) allArguments[1];
        String uri = httpRequest.getRequestLine().getUri();
        Span span = tracer.nextSpan()
                .kind(Span.Kind.CLIENT)
                .name(getRequestURI(uri))
                .tag(Constants.Tags.COMPONENT, Constants.Component.HTTP_CLIENT)
                .tag(Constants.Tags.HTTP_METHOD, httpRequest.getRequestLine().getMethod())
                .tag(Constants.Tags.PEER, httpHost.getHostName() + ":" + port(httpHost))
                .tag(Constants.Tags.URL, buildUrl(httpHost, uri))
                .start(startTime);
        ExtraFieldPropagation.set(span.context(), Constants.ExtraHeaderKey.SERVICE_NAME, Constants.serviceName());
        injector.inject(span.context(), httpRequest);
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

        try {
            HttpResponse response = (HttpResponse) ret;
            if (response != null) {
                StatusLine responseStatusLine = response.getStatusLine();
                if (responseStatusLine != null) {
                    span.getSpan().tag(Constants.Tags.STATUS_CODE, String.valueOf(responseStatusLine.getStatusCode()));
                }
            }
        } finally {
            span.getSpan().finish();
            if (span.getScope() != null) {
                span.getScope().close();
            }
        }
        return ret;
    }

    private String getRequestURI(String uri) throws MalformedURLException {
        if (isUrl(uri)) {
            String requestPath = new URL(uri).getPath();
            return requestPath != null && requestPath.length() > 0 ? requestPath : "/";
        }
        return uri;
    }

    private boolean isUrl(String uri) {
        String lowerUrl = uri.toLowerCase();
        return lowerUrl.startsWith("http") || lowerUrl.startsWith("https");
    }

    private String buildUrl(HttpHost httpHost, String uri) {
        if (!isUrl(uri)) {
            StringBuilder buff = new StringBuilder();
            buff.append(httpHost.getSchemeName().toLowerCase());
            buff.append("://");
            buff.append(httpHost.getHostName());
            buff.append(":");
            buff.append(port(httpHost));
            buff.append(uri);
            return buff.toString();
        }
        return uri;
    }

    private int port(HttpHost httpHost) {
        int port = httpHost.getPort();
        return port > 0 ? port : "https".equals(httpHost.getSchemeName().toLowerCase()) ? 443 : 80;
    }
}
