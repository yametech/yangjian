package cn.ecpark.tool.agent.core.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 日志打印速率控制（信号量实现）
 *
 * @author dengliming
 * @date 2019/11/18
 */
public class SemaphoreLimiter implements IRateLimiter {
    /**
     * 最大允许数量
     */
    private int maxPermits;
    private Semaphore semaphore;
    private TimeUnit timePeriod;
    private ScheduledExecutorService scheduler;

    public static SemaphoreLimiter getDefaultLimiter() {
        return new SemaphoreLimiter(10000, TimeUnit.SECONDS);
    }

    public SemaphoreLimiter(int permits, TimeUnit timePeriod) {
        this.maxPermits = permits;
        this.timePeriod = timePeriod;
        this.semaphore = new Semaphore(maxPermits);
        scheduleRefillPermits();
    }

    /**
     * 定时重放许可
     */
    private void scheduleRefillPermits() {
        scheduler = Executors.newSingleThreadScheduledExecutor(new CustomThreadFactory("limit-schedule", true));
        scheduler.scheduleAtFixedRate(() -> semaphore.release(maxPermits - semaphore.availablePermits()),
                0, 1, timePeriod);
    }

    @Override
    public boolean tryAcquire() {
        return semaphore.tryAcquire();
    }

    @Override
    public void release() {
        semaphore.release();
    }
}
