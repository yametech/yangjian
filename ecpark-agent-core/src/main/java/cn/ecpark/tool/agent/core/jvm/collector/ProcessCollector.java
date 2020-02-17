package cn.ecpark.tool.agent.core.jvm.collector;

import cn.ecpark.tool.agent.core.jvm.metrics.ProcessMetrics;
import cn.ecpark.tool.agent.core.jvm.process.LinuxProcessProvider;
import cn.ecpark.tool.agent.core.jvm.process.NoopProcessProvider;
import cn.ecpark.tool.agent.core.jvm.process.WinProcessProvider;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;
import cn.ecpark.tool.agent.core.util.OSUtil;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

/**
 * @author zcn
 * @date: 2019-10-21
 **/
public final class ProcessCollector implements IMetricsCollector {

    private static final ILogger logger = LoggerFactory.getLogger(ProcessCollector.class);
    private OperatingSystemMXBean operatingSystemMXBean;
    private int processor;
    private long lastCpuTime;
    private long lastCollectTime;

    public ProcessCollector() {
        this.operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.processor = operatingSystemMXBean.getAvailableProcessors();
        this.lastCpuTime = operatingSystemMXBean.getProcessCpuTime();
        this.lastCollectTime = System.nanoTime();
    }

    @Override
    public ProcessMetrics collect() {
        if (OSUtil.isWindows()) {
            return WinProcessProvider.INSTANCE.getProcessMetrics();
        } else if (OSUtil.isLinux()) {
            return LinuxProcessProvider.INSTANCE.getProcessMetrics();
        }
        logger.error(" Fail to collect process metrics , don't support os {}, use {}", OSUtil.OS, NoopProcessProvider.class.getName());
        return NoopProcessProvider.INSTANCE.getProcessMetrics();
    }

    private double calculateCpuUsage() {
        long cpuTime = operatingSystemMXBean.getProcessCpuTime();
        long now = System.nanoTime();
        long cpuCost = cpuTime - lastCpuTime;
        try {
            return cpuCost * 1.0d / ((now - lastCollectTime) * processor) * 100;
        } finally {
            this.lastCpuTime = cpuTime;
            this.lastCollectTime = now;
        }
    }
}
