package cn.ecpark.tool.agent.plugin.hikaricp.context;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.common.StringUtil;
import cn.ecpark.tool.agent.api.interceptor.IConstructorListener;
import cn.ecpark.tool.agent.core.datasource.DataSourceMonitorRegistry;
import cn.ecpark.tool.agent.plugin.hikaricp.monitor.HikariDataSourceMonitor;
import com.zaxxer.hikari.HikariConfig;

/**
 * 拦截连接池构造方法
 *
 * @author dengliming
 * @date 2019/12/21
 */
public class HikariPoolConstructorInterceptor implements IConstructorListener {

    private final DataSourceMonitorRegistry dataSourceMonitorRegistry = DataSourceMonitorRegistry.INSTANCE;

    @Override
    public void constructor(Object thisObj, Object[] allArguments) {
        if (!(allArguments[0] instanceof HikariConfig)) {
            return;
        }
        HikariConfig hikariConfig = (HikariConfig) allArguments[0];
        String jdbcUrl = hikariConfig.getJdbcUrl();
        if (StringUtil.notEmpty(jdbcUrl)) {
            // 为了兼容不同版本该方法有可能被多次执行，所以先判断下当前类上下文是否已经存在该连接池避免重复设置
            HikariDataSourceMonitor hikariDataSourceMonitor = (HikariDataSourceMonitor) ((IContext) thisObj)._getAgentContext(ContextConstants.DATA_SOURCE_CONTEXT_FIELD);
            if (hikariDataSourceMonitor == null) {
                hikariDataSourceMonitor = new HikariDataSourceMonitor(thisObj, jdbcUrl);
                ((IContext) thisObj)._setAgentContext(ContextConstants.DATA_SOURCE_CONTEXT_FIELD, hikariDataSourceMonitor);
                dataSourceMonitorRegistry.register(hikariDataSourceMonitor);
            }
        }
    }
}
