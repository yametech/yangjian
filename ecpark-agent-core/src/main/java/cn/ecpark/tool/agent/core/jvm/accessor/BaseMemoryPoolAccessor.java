package cn.ecpark.tool.agent.core.jvm.accessor;

import cn.ecpark.tool.agent.core.jvm.metrics.MemoryPoolMetrics;
import cn.ecpark.tool.agent.core.jvm.common.MemoryPoolType;

import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zcn
 * @date: 2019-10-25
 **/
public abstract class BaseMemoryPoolAccessor implements IMemoryPoolAccessor {

    private List<MemoryPoolMXBean> beans;

    BaseMemoryPoolAccessor(List<MemoryPoolMXBean> beans) {
        this.beans = beans;
    }

    @Override
    public List<MemoryPoolMetrics> getMemoryPoolList() {
        List<MemoryPoolMetrics> poolList = new LinkedList<>();
        for (MemoryPoolMXBean pool : beans) {
            String name = pool.getName();
            MemoryUsage memoryUsage = pool.getUsage();
            poolList.add(new MemoryPoolMetrics(
                    getPoolType(name),
                    memoryUsage.getInit() >> 10,
                    memoryUsage.getMax() >> 10,
                    memoryUsage.getUsed() >> 10,
                    memoryUsage.getCommitted() >> 10));
        }
        return poolList;
    }

    private MemoryPoolType getPoolType(String name) {
        MemoryPoolType poolType = null;
        if (has(getPermNames(), name)) {
            poolType = MemoryPoolType.PERMGEN;
        } else if (has(getCodeCacheNames(), name)) {
            poolType = MemoryPoolType.CODE_CACHE;
        } else if (has(getEdenNames(), name)) {
            poolType = MemoryPoolType.EDEN;
        } else if (has(getOldNames(), name)) {
            poolType = MemoryPoolType.OLDGEN;
        } else if (has(getSurvivorNames(), name)) {
            poolType = MemoryPoolType.SURVIVOR;
        } else if (has(getMetaspaceNames(), name)) {
            poolType = MemoryPoolType.METASPACE;
        }
        return poolType;
    }

    private boolean has(String[] names, String name) {
        for (String n : names) {
            if (n.equals(name)) {
                return true;
            }
        }
        return false;
    }

    protected abstract String[] getPermNames();

    protected abstract String[] getCodeCacheNames();

    protected abstract String[] getEdenNames();

    protected abstract String[] getOldNames();

    protected abstract String[] getSurvivorNames();

    protected abstract String[] getMetaspaceNames();
}
