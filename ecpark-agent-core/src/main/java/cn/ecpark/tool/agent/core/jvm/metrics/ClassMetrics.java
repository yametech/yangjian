package cn.ecpark.tool.agent.core.jvm.metrics;

/**
 * @author dengliming
 * @date 2019/12/27
 */
public class ClassMetrics implements IMetrics {
    /**
     * 自JVM开始执行到目前已经加载的类的总数
     */
    private final long total;
    /**
     * 当前加载到JVM中的类的数量
     */
    private final long loaded;
    /**
     * 自JVM开始执行到目前已经卸载的类的总数
     */
    private final long unloaded;

    public ClassMetrics(long total, long loaded, long unloaded) {
        this.total = total;
        this.loaded = loaded;
        this.unloaded = unloaded;
    }

    public long getTotal() {
        return total;
    }

    public long getLoaded() {
        return loaded;
    }

    public long getUnloaded() {
        return unloaded;
    }
}
