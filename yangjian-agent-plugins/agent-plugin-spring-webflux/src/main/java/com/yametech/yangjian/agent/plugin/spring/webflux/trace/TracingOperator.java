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
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoOperator;
import reactor.util.context.Context;

import java.util.List;

/**
 * @author dengliming
 * @date 2020/7/17
 */
public class TracingOperator extends MonoOperator<Void, Void> {
    private static final ILogger LOG = LoggerFactory.getLogger(TracingOperator.class);
    private final Tracer tracer;
    private final ServerWebExchange exchange;
    private TraceContext.Extractor<HttpHeaders> extractor;

    TracingOperator(final Mono<? extends Void> source, final ServerWebExchange exchange, final Tracing tracing) {
        super(source);
        this.tracer = tracing.tracer();
        this.exchange = exchange;
        this.extractor = tracing.propagation().extractor((carrier, key) -> {
            List<String> header = carrier.get(key);
            if (header != null && header.size() > 0) {
                return header.get(0);
            }
            return null;
        });
    }

    @Override
    public void subscribe(final CoreSubscriber<? super Void> subscriber) {
        try {
            final Context context = subscriber.currentContext();
            final Span parentSpan = context.<Span>getOrEmpty(Span.class).orElseGet(tracer::currentSpan);
            final ServerHttpRequest request = exchange.getRequest();

            final TraceContext extractedContext = parentSpan != null ? parentSpan.context() : extractor.extract(request.getHeaders()).context();
            Span span = null;
            if (extractedContext != null) {
                span = tracer.newChild(extractedContext);
            } else {
                span = tracer.nextSpan();
            }
            span.name(request.getMethodValue())
                    .kind(Span.Kind.SERVER)
                    .tag(Constants.Tags.COMPONENT, Constants.Component.SPRING_WEBFLUX)
                    .start();

            String parentServiceName = ExtraFieldPropagation.get(span.context(), Constants.ExtraHeaderKey.REFERER_SERVICE);
            if (StringUtil.notEmpty(span.context().parentIdString()) && StringUtil.notEmpty(parentServiceName)) {
                span.tag(Constants.Tags.PARENT_SERVICE_NAME, parentServiceName);
            }
            String agentSign = ExtraFieldPropagation.get(span.context(), Constants.ExtraHeaderKey.AGENT_SIGN);
            if (StringUtil.notEmpty(agentSign)) {
                span.tag(Constants.Tags.AGENT_SIGN, agentSign);
            }
            try (final Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
                source.subscribe(new TracingSubscriber(subscriber, exchange, context, span));
            }
        } catch (Exception e) {
            LOG.error(e, "subscribe error.");
            source.subscribe(subscriber);
        }
    }
}