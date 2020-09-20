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
import com.yametech.yangjian.agent.plugin.mysql.commons.bean.SqlBean;
import com.yametech.yangjian.agent.plugin.mysql.commons.util.MysqlUtil;
import com.yametech.yangjian.agent.plugin.mysql.commons.context.ContextConstants;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 转换JDBC调用事件(StatementImpl)
 *
 * @author dengliming
 * @date 2019/11/25
 */
public class StatementImplConvert implements IMethodAsyncConvert {
	
	@Override
	public List<Object> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        List<String> batchArgs = null;
        String sql = null;
        if (thisObj instanceof IContext) {
            batchArgs = (List<String>) ((IContext) thisObj)._getAgentContext(ContextConstants.MYSQL_BATCH_ARGS_CONTEXT_KEY);
            sql = (String) ((IContext) thisObj)._getAgentContext(ContextConstants.MYSQL_EXEC_SQL_CONTEXT_KEY);
        }

        long now = System.currentTimeMillis();
        List<SqlBean> result = new ArrayList<>();
        // 批量操作
        if (batchArgs != null && !batchArgs.isEmpty()) {
        	batchArgs.forEach(arg -> result.add(new SqlBean(arg, now, now - startTime)));
            batchArgs.clear();
        } else {
            if (allArguments != null && allArguments.length > 0 && allArguments[0] instanceof String) {
                result.add(new SqlBean((String) allArguments[0], now, now - startTime));
            }
            // StatementImpl.executeBatch()时不为空
            else if (StringUtil.notEmpty(sql)) {
                result.add(new SqlBean(sql, now, now - startTime));
            }
        }
        return Arrays.asList(result);
    }
	
	@Override
	public List<TimeEvent> convert(Object eventBean) {
        List<SqlBean> sqlBeans = (List<SqlBean>) eventBean;
        return sqlBeans.stream()
                .filter(sqlBean -> StringUtil.notEmpty(sqlBean.getSql()) && !MysqlUtil.isInExcludeList(sqlBean.getSql()))
        		.map(arg -> MysqlUtil.buildSqlTimeEvent(arg))
        		.flatMap(x -> x.stream())
        		.collect(Collectors.toList());
    }
}
