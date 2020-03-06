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

package com.yametech.yangjian.agent.plugin.druid;

import java.lang.reflect.Method;
import java.util.Map;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.interceptor.IMethodAOP;
import com.yametech.yangjian.agent.plugin.druid.context.ContextConstants;
import com.yametech.yangjian.agent.plugin.druid.monitor.DruidDataSourceMonitor;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public class DataSourceCloseInterceptor implements IMethodAOP {

    @Override
    public BeforeResult before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult beforeResult,
                        Object ret, Throwable t, Map globalVar) {
        if (!(thisObj instanceof IContext)) {
            return ret;
        }
        DruidDataSourceMonitor druidDataSourceMonitor = (DruidDataSourceMonitor) ((IContext) thisObj)._getAgentContext(ContextConstants.DATA_SOURCE_CONTEXT_FIELD);
        druidDataSourceMonitor.setActive(false);
        return ret;

    }
}
