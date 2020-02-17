package cn.ecpark.tool.agent.core.jvm.accessor;

import java.lang.management.MemoryPoolMXBean;
import java.util.List;

/**
 * @author zcn
 * @date: 2019-10-25
 **/
public class SerialMemoryPoolAccessor extends BaseMemoryPoolAccessor {
    public SerialMemoryPoolAccessor(List<MemoryPoolMXBean> beans) {
        super(beans);
    }

    @Override
    protected String[] getPermNames() {
        return new String[]{"Perm Gen", "Compressed Class Space"};
    }

    @Override
    protected String[] getCodeCacheNames() {
        return new String[]{"Code Cache"};
    }

    @Override
    protected String[] getEdenNames() {
        return new String[]{"Eden Space"};
    }

    @Override
    protected String[] getOldNames() {
        return new String[]{"Tenured Gen"};
    }

    @Override
    protected String[] getSurvivorNames() {
        return new String[]{"Survivor Space"};
    }

    @Override
    protected String[] getMetaspaceNames() {
        return new String[]{"Metaspace"};
    }
}
