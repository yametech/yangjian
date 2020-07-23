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
package com.yametech.yangjian.agent.plugin.spring.webflux.trace;

import brave.Tracing;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

import static com.yametech.yangjian.agent.plugin.spring.webflux.context.ContextConstants.SERVER_SPAN_CONTEXT;

/**
 * @author dengliming
 * @date 2020/7/18
 */
public class FilteringWebHandlerInterceptor implements ISpanCreater {

    private Tracing tracing;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracing = tracing;
    }

    @Override
    public BeforeResult before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, Object ret, Throwable t, BeforeResult beforeResult) {
        ServerWebExchange exchange = (ServerWebExchange) allArguments[0];
        // 已经在链路上直接返回
        if (exchange.getAttribute(SERVER_SPAN_CONTEXT) != null) {
            return ret;
        }
        return new TracingOperator((Mono<Void>) ret, (ServerWebExchange) allArguments[0], tracing);
    }
}
