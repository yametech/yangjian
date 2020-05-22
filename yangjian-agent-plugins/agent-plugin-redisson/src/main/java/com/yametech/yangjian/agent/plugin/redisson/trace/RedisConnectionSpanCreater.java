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
package com.yametech.yangjian.agent.plugin.redisson.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MicrosClock;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.common.TraceUtil;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import com.yametech.yangjian.agent.plugin.redisson.context.ContextConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.redisson.client.RedisConnection;
import org.redisson.client.protocol.CommandData;
import org.redisson.client.protocol.CommandsData;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * @author dengliming
 * @date 2020/5/7
 */
public class RedisConnectionSpanCreater implements ISpanCreater<SpanInfo> {

    private static final MicrosClock MICROS_CLOCK = new MicrosClock();
    protected Tracer tracer;
    private ISpanSample spanSample;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracer = tracing.tracer();
        this.spanSample = spanSample;
    }

    @Override
    public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        if (!spanSample.sample()) {
            return null;
        }
        long startTime = MICROS_CLOCK.nowMicros();
        if (startTime == -1L) {
            return null;
        }
        String url = (String) ((IContext) thisObj)._getAgentContext(ContextConstants.REDIS_URL_CONTEXT_KEY);
        if (StringUtil.isEmpty(url)) {
            return null;
        }

        RedisConnection connection = (RedisConnection) thisObj;
        Channel channel = connection.getChannel();
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        String dbInstance = remoteAddress.getAddress().getHostAddress() + ":" + remoteAddress.getPort();

        StringBuilder dbStatement = new StringBuilder();
        String operationName = "Redisson/";
        if (allArguments[0] instanceof CommandsData) {
            operationName = operationName + "Batch";
            CommandsData commands = (CommandsData) allArguments[0];
            for (CommandData commandData : commands.getCommands()) {
                addCommandData(dbStatement, commandData);
                dbStatement.append(";");
            }
        } else if (allArguments[0] instanceof CommandData) {
            CommandData commandData = (CommandData) allArguments[0];
            String command = commandData.getCommand().getName();
            operationName = operationName + command;
            addCommandData(dbStatement, commandData);
        }

        Span span = tracer.nextSpan()
                .kind(Span.Kind.CLIENT)
                .name(operationName)
                .tag(Constants.Tags.URL, url)
                .tag(Constants.Tags.DB_INSTANCE, dbInstance)
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

    private void addCommandData(StringBuilder dbStatement, CommandData commandData) {
        dbStatement.append(commandData.getCommand().getName());
        if (commandData.getParams() != null) {
            for (Object param : commandData.getParams()) {
                dbStatement.append(" ").append(param instanceof ByteBuf ? "?" : String.valueOf(param.toString()));
            }
        }
    }
}
