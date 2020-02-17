package cn.ecpark.tool.agent.core.jvm.collector;

/**
 * @author dengliming
 * @date 2019/12/27
 */
public interface IMetricsCollector<T> {
    T collect();
}
