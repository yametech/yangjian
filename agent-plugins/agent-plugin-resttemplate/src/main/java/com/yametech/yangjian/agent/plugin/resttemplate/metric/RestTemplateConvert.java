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
package com.yametech.yangjian.agent.plugin.resttemplate.metric;

import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.convert.IMethodConvert;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author dengliming
 * @date 2020/6/26
 */
public class RestTemplateConvert implements IMethodConvert {

    @Override
    public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
                                   Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        final URI requestURL = (URI) allArguments[0];
        String requestUrl = requestURL.toString();
        if (StringUtil.isEmpty(requestUrl)) {
            return null;
        }
        TimeEvent event = get(startTime, t);
        event.setIdentify(StringUtil.filterUrlParams(requestUrl));
        return Arrays.asList(event);
    }
}
