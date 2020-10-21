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
package com.yametech.yangjian.agent.plugin.spring.webflux.trace;

import com.yametech.yangjian.agent.api.IEnhanceClassMatch;
import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.configmatch.*;

import java.util.Arrays;

/**
 * 拦截writeTo传递链路上下文信息
 *
 * @author dengliming
 * @date 2020/7/8
 */
public class BodyInserterRequestMatcher implements IEnhanceClassMatch, InterceptorMatcher {

    @Override
    public IConfigMatch classMatch() {
        return new InterfaceMatch("org.springframework.web.reactive.function.client.ClientRequest");
    }

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("org.springframework.web.reactive.function.client.DefaultClientRequestBuilder$BodyInserterRequest"),
                new MethodNameMatch("writeTo"),
                new MethodArgumentIndexMatch(0, "org.springframework.http.client.reactive.ClientHttpRequest"),
                new MethodArgumentIndexMatch(1, "org.springframework.web.reactive.function.client.ExchangeStrategies")

        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
        return new LoadClassKey("com.yametech.yangjian.agent.plugin.spring.webflux.trace.BodyInserterRequestInterceptor");
    }
}
