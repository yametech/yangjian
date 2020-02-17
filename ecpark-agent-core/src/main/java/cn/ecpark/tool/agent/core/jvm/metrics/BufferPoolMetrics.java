package cn.ecpark.tool.agent.core.jvm.metrics;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author dengliming
 * @date 2019/12/27
 */
public class BufferPoolMetrics implements IMetrics {
    /**
     * 缓存池名称
     */
    private final String name;
    /**
     * 缓存池中buffer的数量
     */
    private final long count;
    /**
     * JVM用于此缓冲池的内存估计值（KB）
     */
    private final long memoryUsed;
    /**
     * 缓存池中所有buffer的总容量估计值（KB）
     */
    private final long memoryCapacity;

    public BufferPoolMetrics(String name, long count, long memoryUsed, long memoryCapacity) {
        this.name = name;
        this.count = count;
        this.memoryUsed = memoryUsed;
        this.memoryCapacity = memoryCapacity;
    }

    public String getName() {
        return name;
    }

    public long getCount() {
        return count;
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public long getMemoryCapacity() {
        return memoryCapacity;
    }
}
