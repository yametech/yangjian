package cn.ecpark.tool.agent.plugin.druid;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.bean.BeforeResult;
import cn.ecpark.tool.agent.api.interceptor.IMethodAOP;
import cn.ecpark.tool.agent.core.datasource.DataSourceMonitorRegistry;
import cn.ecpark.tool.agent.plugin.druid.context.ContextConstants;
import cn.ecpark.tool.agent.plugin.druid.monitor.DruidDataSourceMonitor;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public class DataSourceCloseInterceptor implements IMethodAOP {

    private final DataSourceMonitorRegistry dataSourceMonitorRegistry = DataSourceMonitorRegistry.INSTANCE;

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
        dataSourceMonitorRegistry.unregister(druidDataSourceMonitor);
        return ret;

    }
}
