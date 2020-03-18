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
package com.yametech.yangjian.agent.plugin.hikaricp.context;

import java.util.Arrays;

import com.yametech.yangjian.agent.api.IEnhanceClassMatch;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.configmatch.MethodArgumentIndexMatch;
import com.yametech.yangjian.agent.api.pool.IPoolMonitorMatcher;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public class HikariPoolConstructorMatcher implements IPoolMonitorMatcher, IEnhanceClassMatch {

    @Override
    public IConfigMatch classMatch() {
        return new com.yametech.yangjian.agent.api.configmatch.ClassMatch("com.zaxxer.hikari.pool.HikariPool");
    }

    @Override
    public IConfigMatch match() {
        return new com.yametech.yangjian.agent.api.configmatch.CombineAndMatch(Arrays.asList(
                classMatch(),
                new com.yametech.yangjian.agent.api.configmatch.MethodArgumentIndexMatch(0, "com.zaxxer.hikari.HikariConfig"),
                new com.yametech.yangjian.agent.api.configmatch.CombineOrMatch(
                        Arrays.asList(
                                // 2.3.4~
                                new com.yametech.yangjian.agent.api.configmatch.CombineAndMatch(Arrays.asList(
                                        new com.yametech.yangjian.agent.api.configmatch.MethodArgumentNumMatch(1))),
                                // 2.3.4
                                new com.yametech.yangjian.agent.api.configmatch.CombineAndMatch(Arrays.asList(
                                        new com.yametech.yangjian.agent.api.configmatch.MethodArgumentIndexMatch(1, "java.lang.String"),
                                        new MethodArgumentIndexMatch(2, "java.lang.String"),
                                        new com.yametech.yangjian.agent.api.configmatch.MethodArgumentNumMatch(3))))
                )
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
        return new LoadClassKey("com.yametech.yangjian.agent.plugin.hikaricp.context.HikariPoolConstructorInterceptor");
    }
}
