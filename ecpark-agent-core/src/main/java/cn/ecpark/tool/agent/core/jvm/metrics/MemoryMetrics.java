package cn.ecpark.tool.agent.core.jvm.metrics;

/**
 * JVM进程内存占用（单位KB）
 *
 * @author dengliming
 * @date 2019/12/27
 */
public class MemoryMetrics implements IMetrics {
    /**
     * mapped BufferPool
     */
    private long mappedCache;
    /**
     * direct BufferPool
     */
    private long directMemory;
    /**
     * 当前已使用的堆内存
     */
    private long heapUsed;
    /**
     * 当前已经使用的非堆内内存
     */
    private long nonHeapUsed;

    public MemoryMetrics(long mappedCache, long directMemory, long heapUsed, long nonHeapUsed) {
        this.mappedCache = mappedCache;
        this.directMemory = directMemory;
        this.heapUsed = heapUsed;
        this.nonHeapUsed = nonHeapUsed;
    }

    public long getMappedCache() {
        return mappedCache;
    }

    public long getDirectMemory() {
        return directMemory;
    }

    public long getHeapUsed() {
        return heapUsed;
    }

    public long getNonHeapUsed() {
        return nonHeapUsed;
    }
}
