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
package com.yametech.yangjian.agent.plugin.spring.webflux.metric;

import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.convert.IMethodCallbackConvert;
import org.springframework.web.reactive.function.client.ClientRequest;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author dengliming
 * @date 2020/6/27
 */
public class DefaultExchangeFunctionConvert implements IMethodCallbackConvert {

    @Override
    public Object convert(final Consumer<List<TimeEvent>> eventCallback, Object thisObj, long startTime, Object[] allArguments,
                        Method method, Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        ClientRequest clientRequest = (ClientRequest) allArguments[0];
        String url = clientRequest.url().toString();
        return ((Mono) ret)
                .doFinally(res -> eventCallback.accept(buildTimeEvent(startTime, url, t)));
    }

    private List<TimeEvent> buildTimeEvent(long startTime, String identify, Throwable t) {
        TimeEvent timeEvent = get(startTime, t);
        timeEvent.setIdentify(identify);
        return Arrays.asList(timeEvent);
    }
}
