/**
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

package com.yametech.yangjian.agent.plugin.mysql.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.interceptor.IMethodAOP;

/**
 * 增强类为了获取StatementImpl执行的sql（主要原因是因为执行executeBatch之后jdbc会把batchArgs清理掉导致拿不到sql）
 *
 * @author dengliming
 * @date 2019/11/27
 */
public class StatementImplInterceptor implements IMethodAOP {

    @Override
    public BeforeResult before(Object thisObj, Object[] allArguments, Method method) {
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult beforeResult, Object ret, Throwable t, Map globalVar) {
        if (!(thisObj instanceof IContext)) {
            return null;
        }
        List<String> batchArgs = (List<String>) ((IContext) thisObj)._getAgentContext(ContextConstants.MYSQL_BATCH_ARGS_CONTEXT_KEY);
        if (batchArgs == null) {
            batchArgs = new ArrayList<>();
            ((IContext) thisObj)._setAgentContext(ContextConstants.MYSQL_BATCH_ARGS_CONTEXT_KEY, batchArgs);
        }

        batchArgs.add((String) allArguments[0]);
        return ret;
    }
}
