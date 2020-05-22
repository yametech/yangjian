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
package com.yametech.yangjian.agent.plugin.mysql.trace;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import com.yametech.yangjian.agent.plugin.mysql.commons.context.ContextConstants;

import java.lang.reflect.Method;

/**
 * @author dengliming
 * @date 2020/5/2
 */
public class PreparedStatementSpanCreater extends AbstractStatementSpanCreater {

    private static final String SPAN_NAME_FORMAT = "Mysql/PreparedStatement/%s";

    @Override
    public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        if (!spanSample.sample()) {
            return null;
        }
        if (!(thisObj instanceof IContext)) {
            return null;
        }
        String sql = (String) ((IContext) thisObj)._getAgentContext(ContextConstants.MYSQL_EXEC_SQL_CONTEXT_KEY);
        return spanInit(thisObj, String.format(SPAN_NAME_FORMAT, method.getName()), sql);
    }
}
