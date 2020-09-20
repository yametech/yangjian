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
package com.yametech.yangjian.agent.plugin.spring;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.web.method.support.InvocableHandlerMethod;

import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.common.MethodUtil;
import com.yametech.yangjian.agent.api.convert.IMethodConvert;

/**
 * 转换spring controller事件
 *
 * @author liuzhao
 * @Description
 * @date 2019年10月9日 下午3:43:15
 */
public class ControllerConvert implements IMethodConvert {

	@Override
	public List<TimeEvent> convert( Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
		if (!(thisObj instanceof InvocableHandlerMethod)) {
            return null;
        }
        InvocableHandlerMethod handlerMethod = (InvocableHandlerMethod) thisObj;
        TimeEvent event = get(startTime, t);
		event.setIdentify(MethodUtil.getSimpleMethodId(handlerMethod.getMethod()));
		return Arrays.asList(event);
    }
}
