package cn.ecpark.tool.agent.core.jvm.process;

import cn.ecpark.tool.agent.core.jvm.metrics.ProcessMetrics;

/**
 * @author zcn
 * @date: 2019-10-21
 **/
public enum WinProcessProvider {
    INSTANCE;

    public ProcessMetrics getProcessMetrics(){
        return new ProcessMetrics();
    }
}
