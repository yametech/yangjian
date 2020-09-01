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

package com.yametech.yangjian.agent.plugin.jfinal.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import com.jfinal.core.Controller;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MethodUtil;
import com.yametech.yangjian.agent.api.common.MicrosClock;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import static com.yametech.yangjian.agent.api.common.Constants.MAX_TAG_LENGTH;

/**
 * @author dengliming
 * @date 2020/8/31
 */
public class ControllerSpanCreater implements ISpanCreater<SpanInfo> {

    private MicrosClock microsClock;
    protected Tracer tracer;
    private ISpanSample spanSample;
    private TraceContext.Extractor<HttpServletRequest> extractor;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.microsClock = new MicrosClock();
        this.tracer = tracing.tracer();
        this.spanSample = spanSample;
        this.extractor = tracing.propagation().extractor(HttpServletRequest::getHeader);
    }

    @Override
    public BeforeResult before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        HttpServletRequest request = ((Controller) thisObj).getRequest();
        if (!spanSample.sample()) {
            return null;
        }
        long startTime = microsClock.nowMicros();
        if (startTime == -1L) {
            return null;
        }
        Span span = tracer.nextSpan(extractor.extract(request))
                .kind(Span.Kind.SERVER)
                .name(MethodUtil.getSimpleMethodId(method))
                .tag(Constants.Tags.COMPONENT, Constants.Component.JFINAL)
                .tag(Constants.Tags.HTTP_METHOD, request.getMethod())
                .tag(Constants.Tags.PEER, request.getRequestURL().toString())
                .start(startTime);
        String parentServiceName = ExtraFieldPropagation.get(span.context(), Constants.ExtraHeaderKey.REFERER_SERVICE);
        if (StringUtil.notEmpty(span.context().parentIdString()) && StringUtil.notEmpty(parentServiceName)) {
            span.tag(Constants.Tags.PARENT_SERVICE_NAME, parentServiceName);
        }
        String agentSign = ExtraFieldPropagation.get(span.context(), Constants.ExtraHeaderKey.AGENT_SIGN);
        if (StringUtil.notEmpty(agentSign)) {
            span.tag(Constants.Tags.AGENT_SIGN, agentSign);
        }
        final Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap != null && !parameterMap.isEmpty()) {
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                // 这里判断是因为极端情况下key有可能为空 例如：http://xxxxx.com/t?=123
                if (StringUtil.isEmpty(entry.getKey())) {
                    continue;
                }
                span.tag(entry.getKey(), StringUtil.shorten(Arrays.toString(entry.getValue()), MAX_TAG_LENGTH));
            }
        }
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
