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

import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.interceptor.IStaticMethodAOP;
import com.yametech.yangjian.agent.plugin.lettuce.util.RedisUtil;
import io.lettuce.core.RedisURI;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author dengliming
 * @date 2020/7/21
 */
public class MasterSlaveInterceptor implements IStaticMethodAOP {

    @Override
    public BeforeResult before(Object[] allArguments, Method method) throws Throwable {
        @SuppressWarnings("unchecked") Iterable<RedisURI> redisURIs = (Iterable<RedisURI>) allArguments[2];
        if (redisURIs == null) {
            return null;
        }

        StringBuilder peer = new StringBuilder();
        for (RedisURI redisURI : redisURIs) {
            // 过滤哨兵的配置
            if (redisURI.getSentinels() != null && !redisURI.getSentinels().isEmpty()) {
                return null;
            }
            peer.append(redisURI.getHost()).append(":").append(redisURI.getPort()).append(",");
        }
        if (peer.length() > 0) {
            peer.deleteCharAt(peer.length() - 1);
        }
        String redisUrl = peer.toString();
        RedisUtil.reportDependency(redisUrl, Constants.DbMode.MASTER_SLAVE);
        return null;
    }

    @Override
    public Object after(Object[] allArguments, Method method, BeforeResult beforeResult, Object ret, Throwable t, Map globalVar) throws Throwable {
        return ret;
    }
}
