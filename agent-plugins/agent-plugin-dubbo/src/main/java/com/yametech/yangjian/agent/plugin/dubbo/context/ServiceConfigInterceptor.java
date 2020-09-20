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

package com.yametech.yangjian.agent.plugin.dubbo.context;

import com.alibaba.dubbo.config.ServiceConfig;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.interceptor.IMethodAOP;
import com.yametech.yangjian.agent.plugin.dubbo.util.ClassUtil;

import java.lang.reflect.Method;
import java.util.Map;

import static com.yametech.yangjian.agent.plugin.dubbo.context.ContextConstants.INTF_MAPPING_CACHE;

/**
 * @author dengliming
 */
public class ServiceConfigInterceptor implements IMethodAOP {

    @Override
    public BeforeResult before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult beforeResult, Object ret, Throwable t, Map globalVar) throws Throwable {
        ServiceConfig serviceConfig = (ServiceConfig) thisObj;
        String interfaceClass = serviceConfig.getInterface();
        String refClass = ClassUtil.getOriginalClass(serviceConfig.getRef()).getName();
        if (StringUtil.notEmpty(interfaceClass) && StringUtil.notEmpty(refClass)) {
            INTF_MAPPING_CACHE.put(interfaceClass, refClass);
        }
        return ret;
    }
}
