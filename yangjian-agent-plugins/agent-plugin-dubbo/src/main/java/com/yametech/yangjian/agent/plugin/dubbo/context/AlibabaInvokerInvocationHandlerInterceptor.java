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

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;

import static com.yametech.yangjian.agent.plugin.dubbo.context.ContextConstants.DUBBO_GROUP;

/**
 * 增强类为了获取dubbo group
 *
 * @author dengliming
 */
public class AlibabaInvokerInvocationHandlerInterceptor implements IConstructorListener {

    @Override
    public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
        if (!(thisObj instanceof IContext)) {
            return;
        }

        com.alibaba.dubbo.common.URL url = ((com.alibaba.dubbo.rpc.Invoker) allArguments[0]).getUrl();
        ((IContext) thisObj)._setAgentContext(DUBBO_GROUP, url.getParameter("group"));
    }
}
