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

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.interceptor.IMethodAOP;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpRequest;

import java.lang.reflect.Method;
import java.util.Map;

import static com.yametech.yangjian.agent.plugin.spring.webflux.context.ContextConstants.REQUEST_HEADER_CONTEXT_KEY;

/**
 * 拦截writeTo传递链路上下文信息
 *
 * @author dengliming
 * @date 2020/7/8
 */
public class BodyInserterRequestInterceptor implements IMethodAOP {

    @Override
    public BeforeResult before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        ClientHttpRequest clientHttpRequest = (ClientHttpRequest) allArguments[0];
        if (thisObj instanceof IContext) {
            Map<String, String> traceHeaders = (Map<String, String>) ((IContext) thisObj)._getAgentContext(REQUEST_HEADER_CONTEXT_KEY);
            if (traceHeaders != null) {
                HttpHeaders httpHeaders = clientHttpRequest.getHeaders();
                traceHeaders.forEach(httpHeaders::add);
            }
        }
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult beforeResult, Object ret, Throwable t, Map globalVar) throws Throwable {
        return ret;
    }
}
