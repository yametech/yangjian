package cn.ecpark.tool.agent.plugin.mysql.context;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.interceptor.IConstructorListener;

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
