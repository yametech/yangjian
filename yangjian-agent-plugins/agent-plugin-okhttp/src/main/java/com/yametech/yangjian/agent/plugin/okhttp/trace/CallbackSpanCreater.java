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
package com.yametech.yangjian.agent.plugin.okhttp.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import okhttp3.Response;

import java.lang.reflect.Method;

/**
 * @author dengliming
 * @date 2020/4/20
 */
public class CallbackSpanCreater implements ISpanCreater<SpanInfo> {

    protected Tracer tracer;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracer = tracing.tracer();
    }

    @Override
    public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        Span span = tracer.currentSpan();
        if (span == null) {
            return null;
        }
        // onFailure
        if (allArguments[1] instanceof Throwable) {
            span.error((Throwable) allArguments[1]);
        }
        // onResponse
        else if (allArguments[1] instanceof Response) {
            span.tag(Constants.Tags.STATUS_CODE, String.valueOf(((Response) allArguments[1]).code()));
        }

        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, Object ret, Throwable t, BeforeResult<SpanInfo> beforeResult) {
        return ret;
    }
}
