package cn.ecpark.tool.agent.plugin.mysql;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.StringUtil;
import cn.ecpark.tool.agent.api.convert.IMethodAsyncConvert;
import cn.ecpark.tool.agent.plugin.mysql.bean.SqlBean;
import cn.ecpark.tool.agent.plugin.mysql.common.MysqlUtil;
import cn.ecpark.tool.agent.plugin.mysql.context.ContextConstants;

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
        if (thisObj instanceof IContext) {
            batchArgs = (List<String>) ((IContext) thisObj)._getAgentContext(ContextConstants.MYSQL_BATCH_ARGS_CONTEXT_KEY);
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
