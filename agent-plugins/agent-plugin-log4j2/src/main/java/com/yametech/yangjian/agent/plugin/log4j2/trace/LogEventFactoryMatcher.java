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

package com.yametech.yangjian.agent.plugin.log4j2.trace;

import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.configmatch.CombineAndMatch;
import com.yametech.yangjian.agent.api.configmatch.CombineOrMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodArgumentIndexMatch;
import com.yametech.yangjian.agent.api.trace.ITraceMatcher;
import com.yametech.yangjian.agent.api.trace.TraceType;

import java.util.Arrays;

/**
 * @author dengliming
 */
public abstract class LogEventFactoryMatcher implements ITraceMatcher {
    @Override
    public TraceType type() {
        return TraceType.LOG4J;
    }

    protected IConfigMatch methodMatch() {
        return new CombineOrMatch(Arrays.asList(
                new CombineAndMatch(Arrays.asList(
                        new MethodArgumentIndexMatch(0, "java.lang.String"),
                        new MethodArgumentIndexMatch(1, "org.apache.logging.log4j.Marker"),
                        new MethodArgumentIndexMatch(2, "java.lang.String"),
                        new MethodArgumentIndexMatch(3, "org.apache.logging.log4j.Level"),
                        new MethodArgumentIndexMatch(4, "org.apache.logging.log4j.message.Message"),
                        new MethodArgumentIndexMatch(5, "java.util.List"),
                        new MethodArgumentIndexMatch(6, "java.lang.Throwable")
                )),
                new CombineAndMatch(Arrays.asList(
                        new MethodArgumentIndexMatch(0, "java.lang.String"),
                        new MethodArgumentIndexMatch(1, "org.apache.logging.log4j.Marker"),
                        new MethodArgumentIndexMatch(2, "java.lang.String"),
                        new MethodArgumentIndexMatch(3, "java.lang.StackTraceElement"),
                        new MethodArgumentIndexMatch(4, "org.apache.logging.log4j.Level"),
                        new MethodArgumentIndexMatch(5, "org.apache.logging.log4j.message.Message"),
                        new MethodArgumentIndexMatch(6, "java.util.List"),
                        new MethodArgumentIndexMatch(7, "java.lang.Throwable")
                ))
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
        return new LoadClassKey("com.yametech.yangjian.agent.plugin.log4j2.trace.LogEventFactoryInterceptor");
    }
}
