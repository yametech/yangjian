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

import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import com.yametech.yangjian.agent.plugin.jedis.util.RedisUtil;

import java.util.Set;

/**
 * @author dengliming
 * @date 2020/7/16
 */
public class JedisSentinelConstructorInterceptor implements IConstructorListener {

    @Override
    public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
        StringBuilder redisConnInfo = new StringBuilder();
        Set<String> hostAndPorts = (Set<String>) allArguments[1];
        for (String hostAndPort : hostAndPorts) {
            redisConnInfo.append(hostAndPort).append(",");
        }
        String url = String.join(",", hostAndPorts);
        RedisUtil.reportDependency(url, Constants.DbMode.SENTINEL);
    }
}
