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
package com.yametech.yangjian.agent.plugin.jedis.trace;

import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.configmatch.*;
import com.yametech.yangjian.agent.api.trace.ITraceMatcher;
import com.yametech.yangjian.agent.api.trace.TraceType;
import com.yametech.yangjian.agent.plugin.jedis.bean.JedisMethodMatcher;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2020/5/5
 */
public class JedisClusterMethodTraceMatcher implements ITraceMatcher {

    @Override
    public TraceType type() {
        return TraceType.REDIS;
    }

    @Override
    public IConfigMatch match() {
        /**
         * redis.clients.jedis.JedisCluster
         */
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("redis.clients.jedis.JedisCluster"),
                JedisMethodMatcher.INSTANCE.JedisClusterMethodMatch()
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
        return new LoadClassKey("com.yametech.yangjian.agent.plugin.jedis.trace.JedisMethodSpanCreater");
    }
}