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
package com.yametech.yangjian.agent.plugin.okhttp;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.interceptor.IMethodAOP;
import com.yametech.yangjian.agent.plugin.okhttp.context.ContextConstants;
import okhttp3.Request;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author dengliming
 * @date 2020/4/20
 */
public class EnqueueInterceptor implements IMethodAOP {

    @Override
    public BeforeResult<Object> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        if (!(allArguments[0] instanceof IContext)) {
            return null;
        }
        Request request = (Request) ((IContext) thisObj)._getAgentContext(ContextConstants.HTTP_REQUEST_CONTEXT_KEY);
        ((IContext) allArguments[0])._setAgentContext(ContextConstants.HTTP_REQUEST_CONTEXT_KEY, request);
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult beforeResult,
                        Object ret, Throwable t, Map globalVar) throws Throwable {
        return ret;
    }
}
