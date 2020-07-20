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

import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.configmatch.*;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2020/7/16
 */
public class JedisSentinelConstructorMatcher implements InterceptorMatcher {

    private static final ClassMatch CLUSTER_CLASS_MATCH = new ClassMatch("redis.clients.jedis.JedisSentinelPool");

    @Override
    public IConfigMatch match() {
        //public JedisSentinelPool(String masterName, Set<String> sentinels,
        //      final GenericObjectPoolConfig poolConfig, final int connectionTimeout, final int soTimeout,
        //      final String password, final int database, final String clientName)
        return new CombineAndMatch(Arrays.asList(
                CLUSTER_CLASS_MATCH,
                new MethodConstructorMatch(),
                new MethodArgumentIndexMatch(0, "java.lang.String"),
                new MethodArgumentIndexMatch(1, "java.util.Set"),
                new MethodArgumentNumMatch(8)
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
        return new LoadClassKey("com.yametech.yangjian.agent.plugin.jedis.JedisSentinelConstructorInterceptor");
    }
}
