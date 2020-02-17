package cn.ecpark.tool.agent.core.datasource;

import cn.ecpark.tool.agent.api.IDataSourceMonitor;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public enum DataSourceMonitorRegistry {
    INSTANCE;

    private static final ILogger LOGGER = LoggerFactory.getLogger(DataSourceMonitorRegistry.class);
    private final List<IDataSourceMonitor> dataSourceMonitors = new ArrayList<>();

    public boolean register(IDataSourceMonitor dataSourceMonitor) {
        dataSourceMonitors.add(dataSourceMonitor);
        LOGGER.info("DataSourceMonitor[{}/{}] register.", dataSourceMonitor.getType(), dataSourceMonitor.getJdbcUrl());
        return true;
    }

    public boolean unregister(IDataSourceMonitor dataSourceMonitor) {
        dataSourceMonitors.remove(dataSourceMonitor);
        LOGGER.info("DataSourceMonitor[{}/{}] unregister.", dataSourceMonitor.getType(), dataSourceMonitor.getJdbcUrl());
        return true;
    }

    public List<IDataSourceMonitor> getDataSourceMonitors() {
        return dataSourceMonitors;
    }
}
