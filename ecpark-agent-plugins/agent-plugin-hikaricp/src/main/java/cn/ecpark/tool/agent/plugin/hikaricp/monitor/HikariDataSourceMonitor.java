package cn.ecpark.tool.agent.plugin.hikaricp.monitor;

import cn.ecpark.tool.agent.api.IDataSourceMonitor;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author dengliming
 * @date 2019/12/22
 */
public class HikariDataSourceMonitor implements IDataSourceMonitor {

    private static final ILogger LOGGER = LoggerFactory.getLogger(HikariDataSourceMonitor.class);
    private final String jdbcUrl;
    private final Object hikariPool;
    private final Method getActiveConnectionsMethod;
    private final Method getTotalConnectionsMethod;

    public HikariDataSourceMonitor(Object hikariPool, String jdbcUrl) {
        this.hikariPool = hikariPool;
        this.jdbcUrl = jdbcUrl;
        try {
            this.getActiveConnectionsMethod = getActiveConnectionsMethod(hikariPool);
            this.getTotalConnectionsMethod = getTotalConnectionsMethod(hikariPool);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public String getType() {
        return Constants.EventType.HIKARICP;
    }

    @Override
    public int getActiveCount() {
        try {
            Object result = getActiveConnectionsMethod.invoke(hikariPool);
            return (Integer) result;
        } catch (Exception e) {
            LOGGER.error(e, "Execute getActiveCount error.");
        }
        return 0;
    }

    @Override
    public int getMaxTotalConnectionCount() {
        try {
            Object result = getTotalConnectionsMethod.invoke(hikariPool);
            return (Integer) result;
        } catch (Exception e) {
            LOGGER.error(e, "Execute getMaxTotalConnectionCount error.");
        }
        return 0;
    }

    private Method getActiveConnectionsMethod(Object object) throws NoSuchMethodException {
        Method getActiveConnectionsMethod = object.getClass().getMethod("getActiveConnections");
        if (getActiveConnectionsMethod == null) {
            throw new IllegalArgumentException("object must has getActiveConnections method");
        }

        Class<?> returnType = getActiveConnectionsMethod.getReturnType();
        if (int.class != returnType) {
            throw new IllegalArgumentException("invalid return type. expected:int, actual:" + returnType);
        }

        return getActiveConnectionsMethod;
    }

    private Method getTotalConnectionsMethod(Object object) throws NoSuchMethodException {
        Method getTotalConnections = object.getClass().getMethod("getTotalConnections");
        if (getTotalConnections == null) {
            throw new IllegalArgumentException("object must has getTotalConnections method");
        }

        Class<?> returnType = getTotalConnections.getReturnType();
        if (int.class != returnType) {
            throw new IllegalArgumentException("invalid return type. expected:int, actual:" + returnType);
        }

        return getTotalConnections;
    }

    @Override
    public String getJdbcUrl() {
        return jdbcUrl;
    }
}
