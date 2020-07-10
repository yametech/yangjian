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
package com.yametech.yangjian.agent.plugin.spring.webflux.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MicrosClock;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.yametech.yangjian.agent.api.common.BraveUtil.MAP_SETTER;
import static com.yametech.yangjian.agent.plugin.spring.webflux.context.ContextConstants.REQUEST_HEADER_CONTEXT_KEY;
import static com.yametech.yangjian.agent.plugin.spring.webflux.context.ContextConstants.RESPONSE_STATUS_CONTEXT_KEY;

/**
 * @author dengliming
 * @date 2020/6/27
 */
public class DefaultExchangeFunctionInterceptor implements ISpanCreater<SpanInfo> {

    private MicrosClock MICROS_CLOCK;
    protected Tracer tracer;
    private ISpanSample spanSample;
    private TraceContext.Injector<Map<String, String>> injector;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracer = tracing.tracer();
        this.spanSample = spanSample;
        this.injector = tracing.propagation().injector(MAP_SETTER);
        this.MICROS_CLOCK = new MicrosClock();
    }

    @Override
    public BeforeResult before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, Object ret, Throwable t, BeforeResult<SpanInfo> beforeResult) {
        if (!spanSample.sample()) {
            return ret;
        }
        long startTime = MICROS_CLOCK.nowMicros();
        if (startTime == -1L) {
            return ret;
        }
        ClientRequest clientRequest = (ClientRequest) allArguments[0];
        URI url = clientRequest.url();
        Span span = tracer.nextSpan()
                .kind(Span.Kind.SERVER)
                .name(url.toString())
                .tag(Constants.Tags.COMPONENT, Constants.Component.SPRING_WEBCLIENT)
                .tag(Constants.Tags.HTTP_METHOD, clientRequest.method().name())
                .tag(Constants.Tags.PEER, url.getHost() + ":" + url.getPort())
                .start(startTime);

        String parentServiceName = ExtraFieldPropagation.get(span.context(), Constants.ExtraHeaderKey.REFERER_SERVICE);
        Map<String, String> headers = new HashMap<>();
        injector.inject(span.context(), headers);
        if (clientRequest instanceof IContext) {
            ((IContext) clientRequest)._setAgentContext(REQUEST_HEADER_CONTEXT_KEY, headers);
        }

        return ((Mono) ret).doOnSuccess(res -> {
            try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span)) {
                if (t != null) {
                    span.error(t);
                }
                if (StringUtil.notEmpty(span.context().parentIdString()) && StringUtil.notEmpty(parentServiceName)) {
                    span.tag(Constants.Tags.PARENT_SERVICE_NAME, parentServiceName);
                }

                if (res instanceof IContext) {
                    HttpStatus httpStatus = (HttpStatus) ((IContext) res)._getAgentContext(RESPONSE_STATUS_CONTEXT_KEY);
                    if (httpStatus != null) {
                        span.tag(Constants.Tags.STATUS_CODE, Integer.toString(httpStatus.value()));
                    }
                }
            } finally {
                span.finish();
            }
        }).doOnError(error -> {
            try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span)) {
                if (error != null) {
                    span.error((Throwable) error);
                }
                if (StringUtil.notEmpty(span.context().parentIdString()) && StringUtil.notEmpty(parentServiceName)) {
                    span.tag(Constants.Tags.PARENT_SERVICE_NAME, parentServiceName);
                }
            } finally {
                span.finish();
            }
        });
    }
}
