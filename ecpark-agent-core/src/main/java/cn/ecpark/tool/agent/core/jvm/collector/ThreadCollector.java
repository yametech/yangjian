package cn.ecpark.tool.agent.core.jvm.collector;

import cn.ecpark.tool.agent.core.jvm.metrics.ThreadMetrics;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @author dengliming
 * @date 2019/12/27
 */
public final class ThreadCollector implements IMetricsCollector {

    private ThreadMXBean threadMXBean;

    public ThreadCollector() {
        threadMXBean = ManagementFactory.getThreadMXBean();
    }

    @Override
    public ThreadMetrics collect() {
        int news = 0;
        int runnable = 0;
        int blocked = 0;
        int waiting = 0;
        int timedWaiting = 0;
        int terminated = 0;
        ThreadInfo[] threadInfoArr = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 0);
        for (int i = 0; i < threadInfoArr.length; ++i) {
            ThreadInfo threadInfo = threadInfoArr[i];
            if (threadInfo == null) {
                continue;
            }

            Thread.State state = threadInfo.getThreadState();
            switch (state) {
                case NEW:
                    news++;
                    break;
                case RUNNABLE:
                    runnable++;
                    break;
                case BLOCKED:
                    blocked++;
                    break;
                case WAITING:
                    waiting++;
                    break;
                case TIMED_WAITING:
                    timedWaiting++;
                    break;
                case TERMINATED:
                    terminated++;
                    break;
            }
        }

        return new ThreadMetrics(threadMXBean.getTotalStartedThreadCount(), threadMXBean.getThreadCount(),
                threadMXBean.getPeakThreadCount(), threadMXBean.getDaemonThreadCount(), news, runnable,
                blocked, waiting, timedWaiting, terminated);
    }
}
