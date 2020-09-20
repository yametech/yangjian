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
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;

import java.util.List;

import static com.yametech.yangjian.agent.plugin.lettuce.context.ContextConstants.REDIS_URL_CONTEXT_KEY;

/**
 * @author dengliming
 * @date 2020/6/14
 */
public class RedisClientInterceptor implements IConstructorListener {

    @Override
    public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
        RedisURI redisURI = (RedisURI) allArguments[1];
        if (redisURI == null) {
            return;
        }
        RedisClient redisClient = (RedisClient) thisObj;
        if (!(redisClient.getOptions() instanceof IContext)) {
            return;
        }

        String url = null;
        // EMPTY_URI
        if (redisURI.getPort() == 0) {
            // 哨兵模式
            List<RedisURI> redisURIList = redisURI.getSentinels();
            if (redisURIList != null && redisURIList.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (RedisURI redisURI1 : redisURIList) {
                    sb.append(redisURI1.getHost()).append(":").append(redisURI1.getPort()).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                url = sb.toString();
                RedisUtil.reportDependency(url, Constants.DbMode.SENTINEL);
            }
        } else {
            url = redisURI.getHost() + ":" + redisURI.getPort();
            RedisUtil.reportDependency(url, Constants.DbMode.SINGLE);
        }
        ((IContext) redisClient.getOptions())._setAgentContext(REDIS_URL_CONTEXT_KEY, url);
    }
}
