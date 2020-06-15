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
package com.yametech.yangjian.agent.plugin.lettuce.context;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.interceptor.IMethodAOP;
import io.lettuce.core.AbstractRedisClient;

import java.lang.reflect.Method;
import java.util.Map;

import static com.yametech.yangjian.agent.plugin.lettuce.context.ContextConstants.REDIS_URL_CONTEXT_KEY;

/**
 * @author dengliming
 * @date 2020/6/14
 */
public class AbstractRedisClientInterceptor implements IMethodAOP {

    @Override
    public BeforeResult before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        if (!(allArguments[0] instanceof IContext)) {
            return null;
        }
        IContext clientOptions = (IContext) allArguments[0];
        AbstractRedisClient client = (AbstractRedisClient) thisObj;
        if (client.getOptions() == null || ((IContext) client.getOptions())._getAgentContext(REDIS_URL_CONTEXT_KEY) == null) {
            return null;
        }
        clientOptions._setAgentContext(REDIS_URL_CONTEXT_KEY, ((IContext) client.getOptions())._getAgentContext(REDIS_URL_CONTEXT_KEY));
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult beforeResult, Object ret, Throwable t, Map globalVar) throws Throwable {
        return ret;
    }
}
