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
package com.yametech.yangjian.agent.plugin.mongo.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import com.mongodb.MongoNamespace;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MicrosClock;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import com.yametech.yangjian.agent.plugin.mongo.context.ContextConstants;
import com.yametech.yangjian.agent.plugin.mongo.util.MongoUtil;

import java.lang.reflect.Method;

/**
 * @author dengliming
 * @date 2020/5/8
 */
public class OperationExecutorSpanCreater implements ISpanCreater<SpanInfo> {

    private static final MicrosClock MICROS_CLOCK = new MicrosClock();
    private static final String SPAN_NAME_FORMAT = "MongoDB/%s";
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
        long startTime = MICROS_CLOCK.nowMicros();
        if (startTime == -1L) {
            return null;
        }
        String executeMethod = allArguments[0].getClass().getSimpleName();
        String serverUrl = (String) ((IContext) thisObj)._getAgentContext(ContextConstants.MONGO_SERVER_URL);
        if (StringUtil.isEmpty(serverUrl)) {
            return null;
        }

        // 从operation类中获取数据库名
        String database = null;
        if (allArguments[0] instanceof IContext) {
            MongoNamespace namespace = (MongoNamespace) ((IContext) allArguments[0])._getAgentContext(ContextConstants.MONGO_OPERATOR_COLLECTION);
            if (namespace != null) {
                database = namespace.getDatabaseName();
            }
        }

        Span span = tracer.nextSpan()
                .kind(Span.Kind.CLIENT)
                .name(String.format(SPAN_NAME_FORMAT, executeMethod))
                .tag(Constants.Tags.COMPONENT, Constants.Component.MONGO)
                .tag(Constants.Tags.PEER, serverUrl)
                .start(startTime);
        if (StringUtil.notEmpty(database)) {
            span.tag(Constants.Tags.DATABASE, database);
        }
        span.tag(Constants.Tags.DB_STATEMENT, executeMethod + MongoUtil.getTraceParam(allArguments[0]));
        return new BeforeResult<>(null, new SpanInfo(span, tracer.withSpanInScope(span)), null);
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, Object ret, Throwable t,
                        BeforeResult<SpanInfo> beforeResult) {
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
