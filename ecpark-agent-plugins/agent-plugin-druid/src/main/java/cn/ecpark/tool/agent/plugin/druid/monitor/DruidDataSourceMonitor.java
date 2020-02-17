package cn.ecpark.tool.agent.plugin.druid.monitor;

import cn.ecpark.tool.agent.api.IDataSourceMonitor;
import cn.ecpark.tool.agent.api.common.Constants;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public class DruidDataSourceMonitor implements IDataSourceMonitor {

    private final DruidDataSource dataSource;

    public DruidDataSourceMonitor(DruidDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getType() {
        return Constants.EventType.DRUID;
    }

    @Override
    public int getActiveCount() {
        return dataSource == null ? 0 : dataSource.getActiveCount();
    }

    @Override
    public int getMaxTotalConnectionCount() {
        return dataSource == null ? 0 : dataSource.getMaxActive();
    }

    @Override
    public String getJdbcUrl() {
        return dataSource == null ? "Unknown" : dataSource.getUrl();
    }
}
