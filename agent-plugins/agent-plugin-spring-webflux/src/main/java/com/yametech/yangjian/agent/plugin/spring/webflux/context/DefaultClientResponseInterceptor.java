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
package com.yametech.yangjian.agent.plugin.spring.webflux.context;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import org.springframework.http.client.reactive.ClientHttpResponse;

import static com.yametech.yangjian.agent.plugin.spring.webflux.context.ContextConstants.RESPONSE_STATUS_CONTEXT_KEY;

/**
 * 传递StatusCode
 *
 * @author dengliming
 * @date 2020/6/28
 */
public class DefaultClientResponseInterceptor implements IConstructorListener {

    @Override
    public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
        if (thisObj instanceof IContext) {
            ((IContext) thisObj)._setAgentContext(RESPONSE_STATUS_CONTEXT_KEY, ((ClientHttpResponse) allArguments[0]).getStatusCode());
        }
    }
}
