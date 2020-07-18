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

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.convert.IMethodConvert;
import com.yametech.yangjian.agent.plugin.spring.webflux.bean.RequestEvent;
import com.yametech.yangjian.agent.plugin.spring.webflux.context.ContextConstants;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 拦截该方法为了传递请求的开始时间
 *
 * @author dengliming
 * @date 2020/3/17
 */
public class DispatcherHandlerConvert implements IMethodConvert {

    @Override
    public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method,
                                   Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        if (allArguments[0] instanceof IContext) {
            ((IContext) allArguments[0])._setAgentContext(ContextConstants.REQUEST_EVENT_CONTEXT_KEY, new RequestEvent().setStartTime(System.currentTimeMillis()));
        }
        return null;
    }
}