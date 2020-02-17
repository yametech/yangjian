package cn.ecpark.tool.agent.core.jvm.collector;

import cn.ecpark.tool.agent.core.jvm.metrics.ClassMetrics;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

/**
 * @author dengliming
 * @date 2019/12/27
 */
public final class ClassCollector implements IMetricsCollector {

    private ClassLoadingMXBean classLoadingMXBean;

    public ClassCollector() {
        classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
    }

    @Override
    public ClassMetrics collect() {
        return new ClassMetrics(classLoadingMXBean.getTotalLoadedClassCount(), classLoadingMXBean.getLoadedClassCount(), classLoadingMXBean.getUnloadedClassCount());
    }
}
