package cn.ecpark.tool.agent.core.jvm.metrics;

/**
 * @author zcn
 * @date: 2019-10-21
 **/
public class ProcessMetrics implements IMetrics {

    private double cpuUsagePercent = 0.0d;
    /**
     * 进程内存使用
     */
    private double memoryUsage = 0.0d;

    public double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public void setCpuUsagePercent(double cpuUsagePercent) {
        this.cpuUsagePercent = cpuUsagePercent;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
}
