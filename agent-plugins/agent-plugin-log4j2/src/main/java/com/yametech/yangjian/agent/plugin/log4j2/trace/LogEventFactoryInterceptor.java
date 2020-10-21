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

package com.yametech.yangjian.agent.plugin.log4j2.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import org.apache.logging.log4j.ThreadContext;

import java.lang.reflect.Method;

/**
 * 拦截把当前traceId、spanId（如果存在）设置到ThreadContext
 *
 * @author dengliming
 */
public class LogEventFactoryInterceptor implements ISpanCreater {

    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";
    protected Tracer tracer;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracer = tracing.tracer();
    }

    @Override
    public BeforeResult before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan == null) {
            ThreadContext.remove(TRACE_ID);
            ThreadContext.remove(SPAN_ID);
        } else {
            ThreadContext.put(TRACE_ID, currentSpan.context().traceIdString());
            ThreadContext.put(SPAN_ID, currentSpan.context().spanIdString());
        }
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, Object ret, Throwable t, BeforeResult beforeResult) {
        return ret;
    }
}
