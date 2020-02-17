package cn.ecpark.tool.agent.core.jvm.metrics;

/**
 * @author dengliming
 * @date 2019/12/27
 */
public class CPUMetrics implements IMetrics {
    private double usagePercent;
    private int availableProcessors;

    public CPUMetrics(double usagePercent, int availableProcessors) {
        this.usagePercent = usagePercent;
        this.availableProcessors = availableProcessors;
    }

    public double getUsagePercent() {
        return usagePercent;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }
}
