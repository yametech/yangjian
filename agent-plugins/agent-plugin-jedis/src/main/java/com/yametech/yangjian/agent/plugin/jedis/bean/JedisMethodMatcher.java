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
package com.yametech.yangjian.agent.plugin.jedis.bean;

import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.configmatch.ClassMatch;
import com.yametech.yangjian.agent.api.configmatch.CombineAndMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodArgumentIndexMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodArgumentNumMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodNameMatch;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2020/5/6
 */
public enum JedisMethodMatcher {
    INSTANCE;

    public IConfigMatch sendCommandMatcher() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("redis.clients.jedis.Connection"),
                new MethodNameMatch("sendCommand"),
                new MethodArgumentNumMatch(2),
                new MethodArgumentIndexMatch(0, "redis.clients.jedis.commands.ProtocolCommand"),
                new MethodArgumentIndexMatch(1, "byte[][]")
        ));
    }

}
