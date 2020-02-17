package cn.ecpark.tool.agent.core.jvm.collector;

import cn.ecpark.tool.agent.core.jvm.metrics.MemoryMetrics;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * @author zcn
 * @date: 2019-10-17
 **/
public final class MemoryCollector implements IMetricsCollector {

    private static final ILogger logger = LoggerFactory.getLogger(MemoryCollector.class);
    private static final String DIRECT_BUFFER_MBEAN = "java.nio:type=BufferPool,name=direct";
    private static final String MAPPED_BUFFER_MBEAN = "java.nio:type=BufferPool,name=mapped";

    private MemoryMXBean memoryMXBean;
    private MBeanServer mbeanServer;

    public MemoryCollector() {
        memoryMXBean = ManagementFactory.getMemoryMXBean();
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    @Override
    public MemoryMetrics collect() {
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        return new MemoryMetrics(
                collectMappedCache() >> 10,
                collectDirectMemory() >> 10,
                heapMemoryUsage.getUsed() >> 10,
                nonHeapMemoryUsage.getUsed() >> 10);
    }

    public long collectDirectMemory() {
        try {
            ObjectName directPool = new ObjectName(DIRECT_BUFFER_MBEAN);
            return (Long) mbeanServer.getAttribute(directPool, "MemoryUsed");
        } catch (Exception e) {
            logger.error(e, "fail to collect direct memory");
        }
        return 0L;
    }

    public long collectMappedCache() {
        try {
            ObjectName directPool = new ObjectName(MAPPED_BUFFER_MBEAN);
            return (Long) mbeanServer.getAttribute(directPool, "MemoryUsed");
        } catch (Exception e) {
            logger.error(e, "fail to collect mapped cache");
        }
        return 0L;
    }

}
