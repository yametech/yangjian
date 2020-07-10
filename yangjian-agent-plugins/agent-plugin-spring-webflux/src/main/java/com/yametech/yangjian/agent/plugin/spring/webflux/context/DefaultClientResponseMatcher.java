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
package com.yametech.yangjian.agent.plugin.spring.webflux.context;

import com.yametech.yangjian.agent.api.IEnhanceClassMatch;
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
 * 增强DefaultClientResponse为了获取请求响应码，因为该类目前的访问是protected的，不能直接在这里调用
 *
 * @author dengliming
 * @date 2020/6/28
 */
public class DefaultClientResponseMatcher implements IEnhanceClassMatch, InterceptorMatcher {

    @Override
    public IConfigMatch classMatch() {
        return new ClassMatch("org.springframework.web.reactive.function.client.DefaultClientResponse");
    }

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                classMatch(),
                new MethodArgumentIndexMatch(0, "org.springframework.http.client.reactive.ClientHttpResponse"),
                new MethodArgumentIndexMatch(4, "java.util.function.Supplier"),
                new MethodConstructorMatch()
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
        return new LoadClassKey("com.yametech.yangjian.agent.plugin.spring.webflux.context.DefaultClientResponseInterceptor");
    }
}
