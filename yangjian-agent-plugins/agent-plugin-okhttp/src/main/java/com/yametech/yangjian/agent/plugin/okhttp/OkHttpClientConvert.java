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
package com.yametech.yangjian.agent.plugin.okhttp;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.convert.IMethodConvert;
import com.yametech.yangjian.agent.plugin.okhttp.context.ContextConstants;
import okhttp3.Request;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 转换httpclient调用事件
 * <p>
 * 支持版本：okhttp-3.x
 *
 * @author dengliming
 * @date 2019/11/22
 */
public class OkHttpClientConvert implements IMethodConvert {
	
	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        if (!(thisObj instanceof IContext)) {
            return null;
        }

        Request request = (Request) ((IContext) thisObj)._getAgentContext(ContextConstants.HTTP_REQUEST_CONTEXT_KEY);
        if (request == null) {
            return null;
        }
        String requestUrl = request.url().toString();
        if (StringUtil.isEmpty(requestUrl)) {
            return null;
        }
        TimeEvent event = get(startTime);
		event.setIdentify( StringUtil.filterUrlParams(requestUrl));
		return Arrays.asList(event);
    }
}
