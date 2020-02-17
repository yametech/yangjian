package cn.ecpark.tool.agent.core.jvm.accessor;

import cn.ecpark.tool.agent.core.jvm.metrics.MemoryPoolMetrics;

import java.util.List;

/**
 * @author zcn
 * @date: 2019-10-25
 **/
public interface IMemoryPoolAccessor {
    List<MemoryPoolMetrics> getMemoryPoolList();
}
