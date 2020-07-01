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
import com.yametech.yangjian.agent.plugin.jedis.util.RedisUtil;
import redis.clients.jedis.HostAndPort;

import java.util.Set;

import static com.yametech.yangjian.agent.plugin.jedis.context.ContextConstants.REDIS_URL_CONTEXT_KEY;

/**
 * @author dengliming
 * @date 2020/5/5
 */
public abstract class JedisClusterConstructorInterceptor implements IConstructorListener {

    public static class JedisClusterConstructorWithListHostAndPortArgInterceptor extends JedisClusterConstructorInterceptor {
        @Override
        public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
            StringBuilder redisConnInfo = new StringBuilder();
            Set<HostAndPort> hostAndPorts = (Set<HostAndPort>) allArguments[0];
            for (HostAndPort hostAndPort : hostAndPorts) {
                redisConnInfo.append(hostAndPort.toString()).append(",");
            }

            String url = redisConnInfo.toString();
            ((IContext) thisObj)._setAgentContext(REDIS_URL_CONTEXT_KEY, url);
            RedisUtil.reportDependency(url);
        }
    }

    public static class JedisClusterConstructorWithHostAndPortArgInterceptor extends JedisClusterConstructorInterceptor {

        @Override
        public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
            HostAndPort hostAndPort = (HostAndPort) allArguments[0];
            if (hostAndPort != null) {
                String url = hostAndPort.getHost() + ":" + hostAndPort.getPort();
                ((IContext) thisObj)._setAgentContext(REDIS_URL_CONTEXT_KEY, url);
                RedisUtil.reportDependency(url);
            }
        }
    }
}
