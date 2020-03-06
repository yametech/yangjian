package com.yametech.yangjian.agent.util.rate;

import com.yametech.yangjian.agent.util.LogRateLimiter;
import com.yametech.yangjian.agent.util.RateLimiterHolder;
import com.yametech.yangjian.agent.util.SemaphoreLimiter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author dengliming
 * @date 2020/3/6
 */
public class Test {

    @org.junit.Test
    public void testLimiter() throws Exception {
        int threadCount = 1000;
        //List<Long> a = new ArrayList<>(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        //SemaphoreLimiter logLimiter = new SemaphoreLimiter(100, TimeUnit.SECONDS);
        RateLimiterHolder.register(RateLimiterHolder.LOG_RATE_LIMIT_KEY, new SemaphoreLimiter(2, TimeUnit.SECONDS));
        long s = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    boolean tryAcquire = RateLimiterHolder.tryAcquire(RateLimiterHolder.LOG_RATE_LIMIT_KEY);
                    countDownLatch.countDown();
                    //a.add(System.currentTimeMillis() - s);
                    if (tryAcquire) {
                        System.out.println(Thread.currentThread().getName() + ",获取了成功，执行服务...");
                    } else {
                        System.out.println(Thread.currentThread().getName() + ",令牌没有了，待会再来吧...");
                    }
                } finally {
                }
            }).start();
        }

        countDownLatch.await();
        System.out.println("use:" + (System.currentTimeMillis() - s));
    }

    @org.junit.Test
    public void testLimiter2() throws Exception {
        int threadCount = 10;
        //List<Long> a = new ArrayList<>(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        LogRateLimiter logLimiter = new LogRateLimiter(100);
        long s = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                boolean tryAcquire = false;
                try {
                    tryAcquire = logLimiter.tryAcquire();
                    //a.add(System.currentTimeMillis() - s);
                    if (tryAcquire) {
                        //System.out.println(Thread.currentThread().getName() + ",获取了成功，执行服务...");
                    } else {
                        // System.out.println(Thread.currentThread().getName() + ",令牌没有了，待会再来吧...");
                    }
                    countDownLatch.countDown();
                } finally {
                }
            }).start();
        }

        countDownLatch.await();
        System.out.println("use:" + (System.currentTimeMillis() - s));
    }
}
