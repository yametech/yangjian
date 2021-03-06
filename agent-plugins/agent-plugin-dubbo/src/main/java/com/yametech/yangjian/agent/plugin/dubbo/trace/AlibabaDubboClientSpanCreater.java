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
package com.yametech.yangjian.agent.plugin.dubbo.trace;

import brave.Span;
import brave.Span.Kind;
import brave.Tracing;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.BraveUtil;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import com.yametech.yangjian.agent.api.trace.custom.IDubboClientCustom;
import com.yametech.yangjian.agent.api.trace.custom.IDubboCustom;
import com.yametech.yangjian.agent.plugin.dubbo.util.DubboSpanUtil;

import java.lang.reflect.Method;
import java.util.Map;

import static com.yametech.yangjian.agent.api.common.Constants.Tags.DUBBO_GROUP;

public class AlibabaDubboClientSpanCreater extends AlibabaDubboSpanCreater<IDubboClientCustom> {
    private TraceContext.Injector<Map<String, String>> injector;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        super.init(tracing, spanSample);
        this.injector = tracing.propagation().injector(BraveUtil.MAP_SETTER);
    }

    @Override
    public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        RpcContext rpcContext = RpcContext.getContext();
        Invoker<?> invoker = (Invoker<?>) allArguments[0];
        Kind kind = isConsumerSide(rpcContext, invoker.getUrl()) ? Kind.CLIENT : Kind.SERVER;
        if (!kind.equals(Kind.CLIENT)) {
            return null;
        }
        Invocation invocation = (Invocation) allArguments[1];
        IDubboCustom custom = getCustom(invoker.getInterface(), invocation.getMethodName(), invocation.getParameterTypes());
        // 判断是否需要生成span
        if (!generateSpan(invocation.getArguments(), custom)) {
            return null;
        }

        long startTime = MICROS_CLOCK.nowMicros();
        if (startTime == -1L) {
            return null;
        }
        URL url = invoker.getUrl();
        String group = url.getParameter("group");
        boolean isGeneric = Boolean.parseBoolean(url.getParameter("generic"));
        String methodId = null;
        // 泛化调用
        if (isGeneric) {
            methodId = DubboSpanUtil.getGenericInterfaceName(url.getParameter("interface"), invocation.getArguments());
        }
        if (methodId == null) {
            methodId = DubboSpanUtil.getSpanName(invoker.getInterface().getName(), invocation.getMethodName(), invocation.getParameterTypes());
        }
        Span span = tracer.nextSpan()
                .kind(Kind.CLIENT)
                .name(methodId)
                .start(startTime);
        ExtraFieldPropagation.set(span.context(), Constants.ExtraHeaderKey.REFERER_SERVICE, Constants.serviceName());
        if (StringUtil.notEmpty(group)) {
            span.tag(DUBBO_GROUP, group);
            methodId = group + "/" + methodId;
        }
        ExtraFieldPropagation.set(span.context(), Constants.ExtraHeaderKey.AGENT_SIGN, methodId);
        injector.inject(span.context(), rpcContext.getAttachments());
        return spanInit(span, invocation.getArguments(), custom);
    }
}
