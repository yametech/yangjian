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
package com.yametech.yangjian.agent.plugin.redisson.context;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import com.yametech.yangjian.agent.plugin.redisson.util.ClassUtil;
import org.redisson.client.RedisClient;

/**
 * @author dengliming
 * @date 2020/5/7
 */
public class RedisConnectionInterceptor implements IConstructorListener {

    @Override
    public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
        String url = (String) ((IContext) allArguments[0])._getAgentContext(ContextConstants.REDIS_URL_CONTEXT_KEY);
        if (url == null) {
            Object address = ClassUtil.getObjectField(((RedisClient) allArguments[0]).getConfig(), "address");
            String host = (String) ClassUtil.getObjectField(address, "host");
            String port = String.valueOf(ClassUtil.getObjectField(address, "port"));
            url = host + ":" + port;
        }
        ((IContext) thisObj)._setAgentContext(ContextConstants.REDIS_URL_CONTEXT_KEY, url);
    }
}
