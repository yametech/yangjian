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
package com.yametech.yangjian.agent.plugin.okhttp.context;

import com.yametech.yangjian.agent.api.IEnhanceClassMatch;
import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.base.SPI;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.configmatch.*;

import java.util.Arrays;

/**
 *
 * @author dengliming
 * @date 2020/4/23
 */
public class AsyncCallMatcher implements InterceptorMatcher, IEnhanceClassMatch {

    @Override
    public IConfigMatch classMatch() {
        return new ClassMatch("okhttp3.RealCall$AsyncCall");
    }

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                classMatch(),
                new MethodConstructorMatch()
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
        return new LoadClassKey("com.yametech.yangjian.agent.plugin.okhttp.context.AsyncCallInterceptor");
    }
}
