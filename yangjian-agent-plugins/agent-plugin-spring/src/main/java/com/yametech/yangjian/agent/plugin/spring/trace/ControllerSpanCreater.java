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
package com.yametech.yangjian.agent.plugin.spring.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MethodUtil;
import com.yametech.yangjian.agent.api.common.MicrosClock;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import static com.yametech.yangjian.agent.plugin.spring.trace.HandlerMethodInterceptor.CONTEXT_LOCAL;

/**
 * @author dengliming
 * @date 2020/4/20
 */
public class ControllerSpanCreater implements ISpanCreater<SpanInfo> {
    private static final MicrosClock MICROS_CLOCK = new MicrosClock();
    protected Tracer tracer;
    private ISpanSample spanSample;
    private TraceContext.Extractor<HttpServletRequest> extractor;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracer = tracing.tracer();
        this.spanSample = spanSample;
        this.extractor = tracing.propagation().extractor(HttpServletRequest::getHeader);
    }

    @Override
    public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if (request == null) {
                return null;
            }
            if (!spanSample.sample()) {
                return null;
            }
            long startTime = MICROS_CLOCK.nowMicros();
            if (startTime == -1L) {
                return null;
            }
            Span span = tracer.nextSpan(extractor.extract(request))
                    .kind(Span.Kind.SERVER)
                    .name(MethodUtil.getSimpleMethodId(method))
                    .tag(Constants.Tags.COMPONENT, Constants.Component.SPRING_MVC)
                    .tag(Constants.Tags.HTTP_METHOD, request.getMethod())
                    .tag(Constants.Tags.PEER, request.getRequestURL().toString())
                    .start(startTime);
            String parentServiceName = ExtraFieldPropagation.get(span.context(), Constants.ExtraHeaderKey.REFERER_SERVICE);
            if (StringUtil.notEmpty(span.context().parentIdString()) && StringUtil.notEmpty(parentServiceName)) {
                span.tag(Constants.Tags.PARENT_SERVICE_NAME, parentServiceName);
            }
            final Map<String, String[]> parameterMap = request.getParameterMap();
            if (parameterMap != null && !parameterMap.isEmpty()) {
                parameterMap.forEach((k, v) -> span.tag(k, Arrays.toString(v)));
            }
            return new BeforeResult<>(null, new SpanInfo(span, tracer.withSpanInScope(span)), null);
        } catch (Throwable t) {
            // 防止方法异常导致没有执行after方法，所以这里直接remove
            CONTEXT_LOCAL.remove();
            throw t;
        }
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
            HttpServletResponse response = (HttpServletResponse) CONTEXT_LOCAL.get();
            if (response == null) {
                response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            }

            if (response != null) {
                span.getSpan().tag(Constants.Tags.STATUS_CODE, Integer.toString(response.getStatus()));
            }
        } finally {
            span.getSpan().finish();
            if (span.getScope() != null) {
                span.getScope().close();
            }
            CONTEXT_LOCAL.remove();
        }
        return ret;
    }
}
