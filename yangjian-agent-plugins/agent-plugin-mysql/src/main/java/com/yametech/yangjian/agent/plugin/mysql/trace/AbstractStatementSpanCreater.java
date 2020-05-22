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
package com.yametech.yangjian.agent.plugin.mysql.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.internal.Platform;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.common.TraceUtil;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import com.yametech.yangjian.agent.api.trace.custom.IDubboCustom;
import com.yametech.yangjian.agent.plugin.mysql.commons.bean.ConnectionInfo;
import com.yametech.yangjian.agent.plugin.mysql.commons.context.ContextConstants;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * @author dengliming
 * @date 2020/5/20
 */
public abstract class AbstractStatementSpanCreater implements ISpanCreater<SpanInfo> {

    protected Tracer tracer;
    protected ISpanSample spanSample;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracer = tracing.tracer();
        this.spanSample = spanSample;
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

    protected BeforeResult<SpanInfo> spanInit(Object thisObj, String spanName, String sql) {
        ConnectionInfo connectionInfo = (ConnectionInfo) ((IContext) thisObj)._getAgentContext(ContextConstants.MYSQL_CONNECTION_INFO_CONTEXT_KEY);
        if (StringUtil.isEmpty(sql) || connectionInfo == null) {
            return null;
        }
        Span span = tracer.nextSpan()
                .name(spanName)
                .kind(Span.Kind.CLIENT)
                .tag(Constants.Tags.DB_STATEMENT, sql)
                .tag(Constants.Tags.URL, connectionInfo.getUrl())
                .tag(Constants.Tags.DB_INSTANCE, connectionInfo.getDatabaseName())
                .start(TraceUtil.nowMicros());
        return new BeforeResult<>(null, new SpanInfo(span, tracer.withSpanInScope(span)), null);
    }
}
