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
package com.yametech.yangjian.agent.plugin.dubbo;

import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.common.MethodUtil;
import com.yametech.yangjian.agent.api.convert.IMethodConvert;
import com.yametech.yangjian.agent.plugin.dubbo.util.ClassUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 将dubbo服务端调用，转换成实际调用的接口
 * 支持版本：
 * alibaba：dubbo-2.4.10、dubbo-2.5.3、dubbo-2.5.4、dubbo-2.5.5、dubbo-2.5.6、dubbo-2.5.7、dubbo-2.5.10、dubbo-2.6.0、dubbo-2.6.1、dubbo-2.6.2、dubbo-2.6.3、dubbo-2.6.4、dubbo-2.6.5、dubbo-2.6.6、dubbo-2.6.7、dubbo-2.8.3、dubbo-2.8.4
 * apache：dubbo-2.7.0、dubbo-2.7.1、dubbo-2.7.2、dubbo-2.7.3、dubbo-2.7.4
 *
 * @author liuzhao
 * @Description
 * @date 2019年10月9日 下午3:43:15
 */
public class DubboServerConvert implements IMethodConvert {

	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
	    Object proxy = allArguments[0];
        if (proxy == null || allArguments[1] == null || !(allArguments[2] instanceof Class<?>[])) {
            return null;
        }
        String methodName = (String) allArguments[1];
        Class<?>[] parameterTypes = (Class<?>[]) allArguments[2];
        TimeEvent event = get(startTime, t);
		event.setIdentify(MethodUtil.getSimpleMethodId(ClassUtil.getOriginalClass(proxy), methodName, parameterTypes));
		return Arrays.asList(event);
    }


}
