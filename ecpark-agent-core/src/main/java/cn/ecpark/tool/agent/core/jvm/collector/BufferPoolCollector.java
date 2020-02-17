package cn.ecpark.tool.agent.core.jvm.collector;

import cn.ecpark.tool.agent.core.jvm.metrics.BufferPoolMetrics;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dengliming
 * @date 2019/12/27
 */
public final class BufferPoolCollector implements IMetricsCollector<List<BufferPoolMetrics>> {

    private List<BufferPoolMXBean> bufferPoolMXBeans;

    public BufferPoolCollector() {
        bufferPoolMXBeans = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
    }

    @Override
    public List<BufferPoolMetrics> collect() {
        List<BufferPoolMetrics> metricsList = new ArrayList<>(bufferPoolMXBeans.size());
        for (BufferPoolMXBean bufferPoolMXBean : bufferPoolMXBeans) {
            metricsList.add(new BufferPoolMetrics(
                    bufferPoolMXBean.getName(),
                    bufferPoolMXBean.getCount(),
                    bufferPoolMXBean.getMemoryUsed() >> 10,
                    bufferPoolMXBean.getTotalCapacity() >> 10));
        }
        return metricsList;
    }
}
