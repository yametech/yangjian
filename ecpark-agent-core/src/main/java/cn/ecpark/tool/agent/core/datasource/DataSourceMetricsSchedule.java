package cn.ecpark.tool.agent.core.datasource;

import cn.ecpark.tool.agent.api.IDataSourceMonitor;
import cn.ecpark.tool.agent.api.ISchedule;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;
import cn.ecpark.tool.agent.core.util.LogUtil;

import java.util.AbstractMap;
import java.util.List;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public class DataSourceMetricsSchedule implements ISchedule {

    private static final ILogger LOGGER = LoggerFactory.getLogger(DataSourceMetricsSchedule.class);

    @Override
    public int interval() {
        return 1;
    }

    @Override
    public void execute() {
        try {
            List<IDataSourceMonitor> dataSourceMonitors = DataSourceMonitorRegistry.INSTANCE.getDataSourceMonitors();
            for (IDataSourceMonitor monitor : dataSourceMonitors) {
                LogUtil.println("statistic/" + monitor.getType() + "/connectionPool", true,
                        new AbstractMap.SimpleEntry<String, Object>(monitor.getType() + "_active_count", monitor.getActiveCount()),
                        new AbstractMap.SimpleEntry<String, Object>(monitor.getType() + "_max_total", monitor.getMaxTotalConnectionCount()));
            }
        } catch (Exception e) {
            LOGGER.error(e, "DataSourceMetricsScheduler execute error.");
        }
    }
}
