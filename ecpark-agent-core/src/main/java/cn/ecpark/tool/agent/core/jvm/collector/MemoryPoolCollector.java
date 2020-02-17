package cn.ecpark.tool.agent.core.jvm.collector;

import cn.ecpark.tool.agent.core.jvm.accessor.*;
import cn.ecpark.tool.agent.core.jvm.metrics.MemoryPoolMetrics;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;

/**
 * @author zcn
 * @date: 2019-10-25
 **/
public final class MemoryPoolCollector implements IMetricsCollector {

    private static final ILogger logger = LoggerFactory.getLogger(MemoryPoolCollector.class);
    private List<MemoryPoolMXBean> beans;
    private IMemoryPoolAccessor accessor;

    public MemoryPoolCollector() {
        beans = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean bean : beans) {
            IMemoryPoolAccessor a = getAccessor(bean.getName());
            if (a != null) {
                this.accessor = a;
                break;
            }
        }

        if (accessor == null) {
            logger.error("can not match memory pool accessor, use default memory pool accessor");
            accessor = new NoopMemoryPoolAccessor();
        }
    }

    @Override
    public List<MemoryPoolMetrics> collect() {
        return accessor.getMemoryPoolList();
    }

    public IMemoryPoolAccessor getAccessor(String name) {
        if (name.indexOf("PS") > -1) {
            // -XX:+UseParallelOldGC
            return new ParallelMemoryPoolAccessor(beans);
        } else if (name.indexOf("CMS") > -1) {
            //  -XX:+UseConcMarkSweepGC
            return new GMSMemoryPoolAccessor(beans);
        } else if (name.indexOf("G1") > -1) {
            //-XX:+UseG1GC
            return new G1MemoryPoolAccessor(beans);
        } else if (name.equals("Survivor Space")) {
            // -XX:+UseSerialGC
            return new SerialMemoryPoolAccessor(beans);
        }
        return null;
    }

}
