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
import com.yametech.yangjian.agent.api.bean.MethodDefined;

/**
 * 校验是否存在 org.apache.logging.log4j.ThreadContext
 *
 * @author dengliming
 */
public class ThreadContextMatcher implements IConfigMatch {

    private static final boolean EXIST_THREAD_CONTEXT_CLASS = validateThreadContextClass();

    private static boolean validateThreadContextClass() {
        try {
            Class threadContextClass = Class.forName("org.apache.logging.log4j.ThreadContext");
            return true;
        } catch (ClassNotFoundException e) {
        }
        return false;
    }

    @Override
    public boolean isMatch(MethodDefined methodDefined) {
        return EXIST_THREAD_CONTEXT_CLASS;
    }
}
