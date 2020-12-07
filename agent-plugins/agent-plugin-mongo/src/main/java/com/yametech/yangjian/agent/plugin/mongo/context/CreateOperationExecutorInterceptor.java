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

package com.yametech.yangjian.agent.plugin.mongo.context;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.interceptor.IMethodAOP;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 拦截创建OperationExecutor方法传递服务地址
 *
 * @author dengliming
 * @date 2020/5/8
 */
public class CreateOperationExecutorInterceptor implements IMethodAOP {

    @Override
    public BeforeResult before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult beforeResult, Object ret,
                        Throwable t, Map globalVar) throws Throwable {
        if (!(ret instanceof IContext) || !(thisObj instanceof IContext)) {
            return ret;
        }

        String mongoServerUrl = (String) ((IContext) thisObj)._getAgentContext(ContextConstants.MONGO_SERVER_URL);
        if (StringUtil.notEmpty(mongoServerUrl)) {
            // 已经增强OperationExecutor
            ((IContext) ret)._setAgentContext(ContextConstants.MONGO_SERVER_URL, mongoServerUrl);
        }
        return ret;
    }
}
