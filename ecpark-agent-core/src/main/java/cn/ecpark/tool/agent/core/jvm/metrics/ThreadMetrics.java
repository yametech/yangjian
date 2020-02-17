package cn.ecpark.tool.agent.core.jvm.metrics;

/**
 * @author dengliming
 * @date 2019/12/27
 */
public class ThreadMetrics implements IMetrics {

    /**
     * 自JVM启动以来启动过的线程数
     */
    private final long totalStarted;
    /**
     * 当前存活的线程数，包括守护线程和非守护线程
     */
    private final int active;
    /**
     * 当前存活的守护线程数
     */
    private final int daemon;
    /**
     * 正在 JVM 中执行的线程
     */
    private final int runnable;
    /**
     * 受阻塞并等待某个监视器锁的线程数
     */
    private final int blocked;
    /**
     * 无限期地等待另一个线程来执行某一特定操作的线程数
     */
    private final int waiting;
    /**
     * 等待另一个线程来执行取决于指定等待时间的操作的线程处于这种状态数
     */
    private final int timedWaiting;
    /**
     * 已退出的线程数
     */
    private final int terminated;
    /**
     * 自 JVM 启动或峰值重置以来峰值活动线程计数
     */
    private final int peak;
    /**
     * 至今尚未启动的线程数
     */
    private final int news;

    public ThreadMetrics(long totalStarted, int active, int daemon, int runnable, int blocked,
                         int waiting, int timedWaiting, int terminated, int peak, int news) {
        this.totalStarted = totalStarted;
        this.active = active;
        this.daemon = daemon;
        this.runnable = runnable;
        this.blocked = blocked;
        this.waiting = waiting;
        this.timedWaiting = timedWaiting;
        this.terminated = terminated;
        this.peak = peak;
        this.news = news;
    }

    public long getTotalStarted() {
        return totalStarted;
    }

    public int getActive() {
        return active;
    }

    public int getDaemon() {
        return daemon;
    }

    public int getRunnable() {
        return runnable;
    }

    public int getBlocked() {
        return blocked;
    }

    public int getWaiting() {
        return waiting;
    }

    public int getTimedWaiting() {
        return timedWaiting;
    }

    public int getTerminated() {
        return terminated;
    }

    public int getPeak() {
        return peak;
    }

    public int getNews() {
        return news;
    }
}
