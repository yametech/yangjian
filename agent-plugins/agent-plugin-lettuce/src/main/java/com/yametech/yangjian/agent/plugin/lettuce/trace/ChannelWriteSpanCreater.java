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
package com.yametech.yangjian.agent.plugin.lettuce.trace;

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
import io.lettuce.core.protocol.RedisCommand;

import java.lang.reflect.Method;
import java.util.Collection;

import static com.yametech.yangjian.agent.plugin.lettuce.context.ContextConstants.REDIS_URL_CONTEXT_KEY;

/**
 * @author dengliming
 * @date 2020/6/14
 */
public class ChannelWriteSpanCreater implements ISpanCreater<SpanInfo> {

    private MicrosClock MICROS_CLOCK;
    protected Tracer tracer;
    private ISpanSample spanSample;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracer = tracing.tracer();
        this.spanSample = spanSample;
        this.MICROS_CLOCK = new MicrosClock();
    }

    @Override
    public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        if (!spanSample.sample()) {
            return null;
        }
        if (!(thisObj instanceof IContext)) {
            return null;
        }
        String peer = (String) ((IContext) thisObj)._getAgentContext(REDIS_URL_CONTEXT_KEY);
        if (StringUtil.isEmpty(peer)) {
            return null;
        }
        long startTime = MICROS_CLOCK.nowMicros();
        if (startTime == -1L) {
            return null;
        }
        StringBuilder dbStatement = new StringBuilder();
        String operationName = "Lettuce/";
        if (allArguments[0] instanceof RedisCommand) {
            RedisCommand redisCommand = (RedisCommand) allArguments[0];
            String command = redisCommand.getType().name();
            operationName = operationName + command;
            dbStatement.append(command);
        } else if (allArguments[0] instanceof Collection) {
            @SuppressWarnings("unchecked") Collection<RedisCommand> redisCommands = (Collection<RedisCommand>) allArguments[0];
            operationName = operationName + "BATCH_WRITE";
            for (RedisCommand redisCommand : redisCommands) {
                dbStatement.append(redisCommand.getType().name()).append(";");
            }
        }

        Span span = tracer.nextSpan()
                .kind(Span.Kind.CLIENT)
                .name(operationName)
                .tag(Constants.Tags.COMPONENT, Constants.Component.LETTUCE)
                .tag(Constants.Tags.PEER, peer)
                .tag(Constants.Tags.DB_STATEMENT, StringUtil.shorten(dbStatement.toString(), 50))
                .start(startTime);
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
