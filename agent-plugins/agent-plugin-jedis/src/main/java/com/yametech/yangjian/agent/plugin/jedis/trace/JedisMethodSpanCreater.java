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
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MicrosClock;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import redis.clients.jedis.Connection;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.commands.ProtocolCommand;

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
        if (!spanSample.sample()) {
            return null;
        }
        long startTime = MICROS_CLOCK.nowMicros();
        if (startTime == -1L) {
            return null;
        }
        String command = getStringCommand((ProtocolCommand) allArguments[0]);
        if (StringUtil.isEmpty(command)) {
            return null;
        }
        Span span = tracer.nextSpan()
                .kind(Span.Kind.CLIENT)
                .name(String.format(SPAN_NAME_FORMAT, command))
                .tag(Constants.Tags.COMPONENT, Constants.Component.JEDIS)
                .tag(Constants.Tags.PEER, getPeer((Connection) thisObj))
                .start(startTime);
        byte[][] args = (byte[][]) allArguments[1];
        if (args != null && args.length > 0) {
            span.tag(Constants.Tags.DB_STATEMENT, command + " " + StringUtil.encode(args[0]));
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

    private String getStringCommand(ProtocolCommand command) {
        if (command instanceof Protocol.Command) {
            return ((Protocol.Command) command).name();
        } else {
            // Protocol.Command is the only implementation in the Jedis lib as of 3.1 but this will save
            // us if that changes
            return StringUtil.encode(command.getRaw());
        }
    }

    private String getPeer(Connection connection) {
        return connection.getHost() + ":" + connection.getPort();
    }
}
