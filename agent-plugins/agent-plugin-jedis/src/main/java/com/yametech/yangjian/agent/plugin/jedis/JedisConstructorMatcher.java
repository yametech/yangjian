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
import com.yametech.yangjian.agent.api.configmatch.ClassMatch;
import com.yametech.yangjian.agent.api.configmatch.CombineAndMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodArgumentIndexMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodConstructorMatch;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2020/5/5
 */
public abstract class JedisConstructorMatcher implements InterceptorMatcher {

    private static final ClassMatch CLASS_MATCH = new ClassMatch("redis.clients.jedis.Jedis");

    public static class JedisConstructorWithUriArgMatcher extends JedisConstructorMatcher {

        @Override
        public IConfigMatch match() {
            return new CombineAndMatch(Arrays.asList(
                    CLASS_MATCH,
                    new MethodConstructorMatch(),
                    new MethodArgumentIndexMatch(0, "java.net.URI")
            ));
        }

        @Override
        public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
            return new LoadClassKey("com.yametech.yangjian.agent.plugin.jedis.JedisConstructorInterceptor$JedisConstructorWithUriArgInterceptor");
        }
    }

    public static class JedisConstructorWithStringArgMatcher extends JedisConstructorMatcher {

        @Override
        public IConfigMatch match() {
            return new CombineAndMatch(Arrays.asList(
                    CLASS_MATCH,
                    new MethodConstructorMatch(),
                    new MethodArgumentIndexMatch(0, "java.lang.String")
            ));
        }

        @Override
        public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
            return new LoadClassKey("com.yametech.yangjian.agent.plugin.jedis.JedisConstructorInterceptor$JedisConstructorWithStringArgInterceptor");
        }
    }

    public static class JedisConstructorWithShardInfoArgMatcher extends JedisConstructorMatcher {

        @Override
        public IConfigMatch match() {
            return new CombineAndMatch(Arrays.asList(
                    CLASS_MATCH,
                    new MethodConstructorMatch(),
                    new MethodArgumentIndexMatch(0, "redis.clients.jedis.JedisShardInfo")
            ));
        }

        @Override
        public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
            return new LoadClassKey("com.yametech.yangjian.agent.plugin.jedis.JedisConstructorInterceptor$JedisConstructorWithShardInfoArgInterceptor");
        }
    }
}
