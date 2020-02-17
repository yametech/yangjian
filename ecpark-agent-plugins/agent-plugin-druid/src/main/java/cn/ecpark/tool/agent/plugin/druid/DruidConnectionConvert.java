package cn.ecpark.tool.agent.plugin.druid;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;
import cn.ecpark.tool.agent.plugin.druid.context.ContextConstants;
import cn.ecpark.tool.agent.plugin.druid.monitor.DruidDataSourceMonitor;
import cn.ecpark.tool.agent.util.Utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author dengliming
 * @date 2019/12/22
 */
public class DruidConnectionConvert implements IMethodConvert {

    @Override
    public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method,
                                   Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        if (!(thisObj instanceof IContext)) {
            return null;
        }
        DruidDataSourceMonitor druidDataSourceMonitor = (DruidDataSourceMonitor) ((IContext) thisObj)._getAgentContext(ContextConstants.DATA_SOURCE_CONTEXT_FIELD);
        TimeEvent event = get(startTime);
        event.setIdentify(Utils.parseJdbcUrl(druidDataSourceMonitor.getJdbcUrl()) + Constants.IDENTIFY_SEPARATOR + Constants.DbOperation.GET_CONNECTION);
        return Arrays.asList(event);
    }
}
