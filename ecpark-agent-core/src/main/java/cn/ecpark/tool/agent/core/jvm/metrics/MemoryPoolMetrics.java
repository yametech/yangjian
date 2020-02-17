package cn.ecpark.tool.agent.core.jvm.metrics;

import cn.ecpark.tool.agent.core.jvm.common.MemoryPoolType;

/**
 * JVM各内存区域资源占用
 *
 * @author dengliming
 * @date 2019/12/27
 */
public class MemoryPoolMetrics implements IMetrics {

    private MemoryPoolType type;
    /**
     * 以下单位都是KB
     */
    private long init = 0L;
    private long max = 0L;
    private long used = 0L;
    private long commited = 0L;

    public MemoryPoolMetrics(MemoryPoolType type) {
        this.type = type;
    }

    public MemoryPoolMetrics(MemoryPoolType type, long init, long max, long used, long commited) {
        this.type = type;
        this.init = init;
        this.max = max;
        this.used = used;
        this.commited = commited;
    }

    public MemoryPoolType getType() {
        return type;
    }

    public long getInit() {
        return init;
    }

    public long getMax() {
        return max;
    }

    public long getUsed() {
        return used;
    }

    public long getCommited() {
        return commited;
    }
}
