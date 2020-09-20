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
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import com.yametech.yangjian.agent.plugin.lettuce.util.RedisUtil;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;

import static com.yametech.yangjian.agent.plugin.lettuce.context.ContextConstants.REDIS_URL_CONTEXT_KEY;

/**
 * @author dengliming
 * @date 2020/6/14
 */
public class RedisClusterClientInterceptor implements IConstructorListener {

    @Override
    public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
        @SuppressWarnings("unchecked") Iterable<RedisURI> redisURIs = (Iterable<RedisURI>) allArguments[1];
        if (redisURIs == null) {
            return;
        }
        RedisClusterClient redisClusterClient = (RedisClusterClient) thisObj;
        StringBuilder peer = new StringBuilder();
        for (RedisURI redisURI : redisURIs) {
            peer.append(redisURI.getHost()).append(":").append(redisURI.getPort()).append(",");
        }
        if (peer.length() > 0) {
            peer.deleteCharAt(peer.length() - 1);
        }
        String redisUrl = peer.toString();
        ((IContext) redisClusterClient.getOptions())._setAgentContext(REDIS_URL_CONTEXT_KEY, redisUrl);
        RedisUtil.reportDependency(redisUrl, Constants.DbMode.CLUSTER);
    }
}
