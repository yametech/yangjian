package cn.ecpark.tool.agent.api;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public interface IDataSourceMonitor {

    String getType();

    /**
     * 活跃连接数
     *
     * @return
     */
    int getActiveCount();

    /**
     * 最大连接数
     *
     * @return
     */
    int getMaxTotalConnectionCount();

    default String getJdbcUrl() {
        return "Unknown";
    }
}
