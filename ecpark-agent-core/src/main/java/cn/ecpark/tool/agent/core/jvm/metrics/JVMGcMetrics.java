package cn.ecpark.tool.agent.core.jvm.metrics;

/**
 * GC度量指标
 *
 * @author dengliming
 * @date 2019/12/27
 */
public class JVMGcMetrics implements IMetrics {

    /**
     * 一个时间片内累计 YoungGC 次数
     */
    private final long youngGcCount;
    /**
     * 一个时间片内累计 YoungGC 时间
     */
    private final long youngGcTime;
    /**
     * 一个时间片内 YoungGC 平均时间
     */
    private final double avgYoungGcTime;
    /**
     * 一个时间片内累计 OldGC 次数
     */
    private final long fullGcCount;
    /**
     * 一个时间片内累计 OldGC 时间
     */
    private final long fullGcTime;

    public JVMGcMetrics(long youngGcCount, long youngGcTime, long fullGcCount, long fullGcTime) {
        this.youngGcCount = youngGcCount;
        this.youngGcTime = youngGcTime;
        this.fullGcCount = fullGcCount;
        this.fullGcTime = fullGcTime;
        this.avgYoungGcTime = youngGcCount > 0L ? ((double) youngGcTime) / youngGcCount : 0d;
    }

    public long getYoungGcCount() {
        return youngGcCount;
    }

    public long getYoungGcTime() {
        return youngGcTime;
    }

    public double getAvgYoungGcTime() {
        return avgYoungGcTime;
    }

    public long getFullGcCount() {
        return fullGcCount;
    }

    public long getFullGcTime() {
        return fullGcTime;
    }
}
