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
package com.yametech.yangjian.agent.plugin.mysql;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.convert.IMethodAsyncConvert;
import com.yametech.yangjian.agent.plugin.mysql.bean.SqlBean;
import com.yametech.yangjian.agent.plugin.mysql.common.MysqlUtil;
import com.yametech.yangjian.agent.plugin.mysql.context.ContextConstants;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 转换JDBC调用事件(PreparedStatement)
 *
 * @author dengliming
 * @date 2019/11/27
 */
public class PreparedStatementConvert implements IMethodAsyncConvert {

    @Override
    public List<Object> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
                                Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        if (!(thisObj instanceof IContext)) {
            return null;
        }
        String sql = (String) ((IContext) thisObj)._getAgentContext(ContextConstants.MYSQL_EXEC_SQL_CONTEXT_KEY);
        if (StringUtil.isEmpty(sql)) {
            return null;
        }
        long now = System.currentTimeMillis();
        return Arrays.asList(new SqlBean(sql, now, now - startTime));
    }

    @Override
    public List<TimeEvent> convert(Object eventBean) {
        SqlBean sqlBean = (SqlBean) eventBean;
        if (sqlBean == null || MysqlUtil.isInExcludeList(sqlBean.getSql())) {
            return null;
        }
        return MysqlUtil.buildSqlTimeEvent(sqlBean);
    }

}