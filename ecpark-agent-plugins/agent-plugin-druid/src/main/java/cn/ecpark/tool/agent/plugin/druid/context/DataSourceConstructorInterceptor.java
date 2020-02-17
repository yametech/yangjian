package cn.ecpark.tool.agent.plugin.druid.context;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.interceptor.IConstructorListener;
import cn.ecpark.tool.agent.core.datasource.DataSourceMonitorRegistry;
import cn.ecpark.tool.agent.plugin.druid.monitor.DruidDataSourceMonitor;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public class DataSourceConstructorInterceptor implements IConstructorListener {

    private final DataSourceMonitorRegistry dataSourceMonitorRegistry = DataSourceMonitorRegistry.INSTANCE;

    @Override
    public void constructor(Object thisObj, Object[] allArguments) {
        DruidDataSourceMonitor druidDataSourceMonitor = new DruidDataSourceMonitor((DruidDataSource) thisObj);
        ((IContext) thisObj)._setAgentContext(ContextConstants.DATA_SOURCE_CONTEXT_FIELD, druidDataSourceMonitor);
        dataSourceMonitorRegistry.register(druidDataSourceMonitor);
    }
}
