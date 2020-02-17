package cn.ecpark.tool.agent.plugin.mysql;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.StringUtil;
import cn.ecpark.tool.agent.api.convert.IMethodAsyncConvert;
import cn.ecpark.tool.agent.plugin.mysql.bean.SqlBean;
import cn.ecpark.tool.agent.plugin.mysql.common.MysqlUtil;
import cn.ecpark.tool.agent.plugin.mysql.context.ContextConstants;

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