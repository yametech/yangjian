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

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;

/**
 * 增强类为了获取jdbcPreparedStatement执行的sql
 *
 * @author dengliming
 * @date 2019/11/27
 */
public class PreparedStatementInterceptor implements IConstructorListener {

    @Override
    public void constructor(Object thisObj, Object[] allArguments) {
        if (allArguments != null && allArguments.length > 2) {
            String sql = (String) allArguments[1];
            ((IContext) thisObj)._setAgentContext(ContextConstants.MYSQL_EXEC_SQL_CONTEXT_KEY, sql);
        }
    }
}
