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
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.plugin.spring.webflux.bean.RequestEvent;
import com.yametech.yangjian.agent.plugin.spring.webflux.context.ContextConstants;
import org.reactivestreams.Subscription;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

import java.util.Optional;

import static com.yametech.yangjian.agent.plugin.spring.webflux.context.ContextConstants.SERVER_SPAN_CONTEXT;

/**
 * @author dengliming
 * @date 2020/7/17
 */
public class TracingSubscriber implements CoreSubscriber<Void> {
    private static final ILogger LOG = LoggerFactory.getLogger(TracingSubscriber.class);
    private final CoreSubscriber<? super Void> subscriber;
    private final ServerWebExchange exchange;
    private final Context context;
    private final Span span;

    TracingSubscriber(final CoreSubscriber<? super Void> subscriber, final ServerWebExchange exchange,
                      final Context context, final Span span) {
        this.subscriber = subscriber;
        this.exchange = exchange;
        this.context = context.put(Span.class, span);
        this.span = span;
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        try {
            final ServerHttpRequest request = exchange.getRequest();
            span.tag(Constants.Tags.HTTP_METHOD, request.getMethodValue());
            span.tag(Constants.Tags.PEER, request.getURI().toString());
        } catch (Throwable e) {
            LOG.error(e, "onSubscribe error.");
        }
        subscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(final Void aVoid) {
        // Never called
        subscriber.onNext(aVoid);
    }

    @Override
    public void onError(final Throwable throwable) {
        try {
            setSpanName();
            span.error(throwable);
            exchange.getAttributes().remove(SERVER_SPAN_CONTEXT);
        } catch (Throwable e) {
            LOG.error(e, "onError error.");
        } finally {
            span.finish();
        }
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        try {
            setSpanName();
            Optional.ofNullable(exchange.getResponse().getStatusCode())
                    .ifPresent(httpStatus -> span.tag(Constants.Tags.STATUS_CODE, String.valueOf(httpStatus.value())));
            exchange.getAttributes().remove(SERVER_SPAN_CONTEXT);
        } catch (Throwable e) {
            LOG.error(e, "onComplete error.");
        } finally {
            span.finish();
        }
        subscriber.onComplete();
    }

    @Override
    public Context currentContext() {
        return context;
    }

    private void setSpanName() {
        String spanName = null;
        if (exchange instanceof IContext) {
            RequestEvent requestEvent = (RequestEvent) ((IContext) exchange)._getAgentContext(ContextConstants.REQUEST_EVENT_CONTEXT_KEY);
            if (requestEvent != null) {
                spanName = requestEvent.getMethodName();
            }
        }

        if (StringUtil.isEmpty(spanName)) {
            Object pathPattern = exchange.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            if (pathPattern != null) {
                spanName = ((PathPattern) pathPattern).getPatternString();
            }
        }

        if (StringUtil.notEmpty(spanName)) {
            span.name(spanName);
        }
    }
}