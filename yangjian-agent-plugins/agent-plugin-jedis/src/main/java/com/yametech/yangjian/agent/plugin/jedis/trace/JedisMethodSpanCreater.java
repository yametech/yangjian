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
package com.yametech.yangjian.agent.plugin.jedis.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MicrosClock;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import com.yametech.yangjian.agent.plugin.jedis.context.ContextConstants;

import java.lang.reflect.Method;

/**
 * @author dengliming
 * @date 2020/5/5
 */
public class JedisMethodSpanCreater implements ISpanCreater<SpanInfo> {

    private static final MicrosClock MICROS_CLOCK = new MicrosClock();
    private static final String SPAN_NAME_FORMAT = "Jedis/%s";
    protected Tracer tracer;
    private ISpanSample spanSample;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracer = tracing.tracer();
        this.spanSample = spanSample;
    }

    @Override
    public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        if (!(thisObj instanceof IContext)) {
            return null;
        }
        if (!spanSample.sample()) {
            return null;
        }
        String url = (String) ((IContext) thisObj)._getAgentContext(ContextConstants.REDIS_URL_CONTEXT_KEY);
        if (StringUtil.isEmpty(url)) {
            return null;
        }
        long startTime = MICROS_CLOCK.nowMicros();
        if (startTime == -1L) {
            return null;
        }
        Span span = tracer.nextSpan()
                .kind(Span.Kind.CLIENT)
                .name(String.format(SPAN_NAME_FORMAT, method.getName()))
                .tag(Constants.Tags.COMPONENT, Constants.Component.JEDIS)
                .tag(Constants.Tags.PEER, url)
                .start(startTime);
        if (allArguments.length > 0 && allArguments[0] instanceof String) {
            span.tag(Constants.Tags.DB_STATEMENT, method.getName() + " " + allArguments[0]);
        } else if (allArguments.length > 0 && allArguments[0] instanceof byte[]) {
            span.tag(Constants.Tags.DB_STATEMENT, method.getName());
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
