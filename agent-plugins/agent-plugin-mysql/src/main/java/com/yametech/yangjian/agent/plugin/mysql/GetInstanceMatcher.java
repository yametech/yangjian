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
package com.yametech.yangjian.agent.plugin.mysql;

import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.configmatch.*;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2020/5/17
 */
public abstract class GetInstanceMatcher implements InterceptorMatcher {

    /**
     * for 6.x
     */
    public static class GetInstanceMatcher6x extends GetInstanceMatcher {
        @Override
        public IConfigMatch match() {
            return new CombineAndMatch(Arrays.asList(
                    new ClassMatch("com.mysql.cj.jdbc.ConnectionImpl"),
                    new MethodStatisticMatch(),
                    new MethodNameMatch("getInstance"),
                    new MethodArgumentIndexMatch(0, "com.mysql.cj.core.conf.url.HostInfo"))
            );
        }

        @Override
        public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
            return new LoadClassKey("com.yametech.yangjian.agent.plugin.mysql.GetInstanceInterceptor$GetInstanceInterceptor6x");
        }
    }

    /**
     * for 5.x
     */
    public static class GetInstanceMatcher5x extends GetInstanceMatcher {
        @Override
        public IConfigMatch match() {
            return new CombineAndMatch(Arrays.asList(
                    new ClassMatch("com.mysql.jdbc.ConnectionImpl"),
                    new MethodStatisticMatch(),
                    new MethodNameMatch("getInstance"))
            );
        }

        @Override
        public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
            return new LoadClassKey("com.yametech.yangjian.agent.plugin.mysql.GetInstanceInterceptor$GetInstanceInterceptor5x");
        }
    }

    /**
     * for 8.x
     */
    public static class GetInstanceMatcher8x extends GetInstanceMatcher {
        @Override
        public IConfigMatch match() {
            return new CombineAndMatch(Arrays.asList(
                    new ClassMatch("com.mysql.cj.jdbc.ConnectionImpl"),
                    new MethodStatisticMatch(),
                    new MethodNameMatch("getInstance"),
                    new MethodArgumentIndexMatch(0, "com.mysql.cj.conf.HostInfo"))
            );
        }

        @Override
        public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
            return new LoadClassKey("com.yametech.yangjian.agent.plugin.mysql.v8.GetInstanceInterceptor");
        }
    }
}
