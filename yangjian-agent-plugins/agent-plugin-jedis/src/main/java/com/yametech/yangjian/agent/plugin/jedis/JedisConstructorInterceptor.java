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
package com.yametech.yangjian.agent.plugin.jedis;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import com.yametech.yangjian.agent.plugin.jedis.context.ContextConstants;
import redis.clients.jedis.JedisShardInfo;

import java.net.URI;

import static com.yametech.yangjian.agent.plugin.jedis.context.ContextConstants.REDIS_URL_CONTEXT_KEY;

/**
 * @author dengliming
 * @date 2020/5/5
 */
public abstract class JedisConstructorInterceptor implements IConstructorListener {

    public static class JedisConstructorWithUriArgInterceptor extends JedisConstructorInterceptor {
        @Override
        public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
            URI uri = (URI) allArguments[0];
            if (uri != null) {
                ((IContext) thisObj)._setAgentContext(REDIS_URL_CONTEXT_KEY, uri.getHost() + ":" + uri.getPort());
            }
        }
    }


    public static class JedisConstructorWithStringArgInterceptor extends JedisConstructorInterceptor {
        @Override
        public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
            String host = (String) allArguments[0];
            String port = "6379";
            if (allArguments.length > 1) {
                port = String.valueOf(allArguments[1]);
            }

            ((IContext) thisObj)._setAgentContext(REDIS_URL_CONTEXT_KEY, host + ":" + port);
        }
    }

    public static class JedisConstructorWithShardInfoArgInterceptor extends JedisConstructorInterceptor {
        @Override
        public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
            JedisShardInfo shardInfo = (JedisShardInfo) allArguments[0];
            if (shardInfo != null) {
                ((IContext) thisObj)._setAgentContext(ContextConstants.REDIS_URL_CONTEXT_KEY, shardInfo.getHost() + ":" + shardInfo.getPort());
            }
        }
    }
}
