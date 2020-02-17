package cn.ecpark.tool.agent.core.jvm.accessor;

import cn.ecpark.tool.agent.core.jvm.metrics.MemoryPoolMetrics;
import cn.ecpark.tool.agent.core.jvm.common.MemoryPoolType;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zcn
 * @date: 2019-10-25
 **/
public class NoopMemoryPoolAccessor implements IMemoryPoolAccessor {
    @Override
    public List<MemoryPoolMetrics> getMemoryPoolList() {
        List<MemoryPoolMetrics> poolList = new LinkedList<>();
        poolList.add(new MemoryPoolMetrics(MemoryPoolType.CODE_CACHE));
        poolList.add(new MemoryPoolMetrics(MemoryPoolType.METASPACE));
        poolList.add(new MemoryPoolMetrics(MemoryPoolType.EDEN));
        poolList.add(new MemoryPoolMetrics(MemoryPoolType.OLDGEN));
        poolList.add(new MemoryPoolMetrics(MemoryPoolType.PERMGEN));
        poolList.add(new MemoryPoolMetrics(MemoryPoolType.SURVIVOR));
        return poolList;
    }
}
