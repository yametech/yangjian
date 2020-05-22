package com.yametech.yangjian.agent.api.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MicrosClock implements Runnable {
    private static ScheduledExecutorService service = Executors.newScheduledThreadPool(1, new CustomThreadFactory("ClockTime-check-schedule", true));
    private static final int CALIBRATION_MILLIS = 1000;// 偏差超过该毫秒数时才会自动校准

    private long startMicrosTime;
    private long startMicros;
    private long previous;
    private volatile boolean checkTime;

    public MicrosClock() {
        init();
        service.scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
    }

    private void init() {
        startMicros = System.nanoTime() / 1000;
        startMicrosTime = System.currentTimeMillis() * 1000 + Math.abs(startMicros % 1000);
    }

    /**
     * 获取下一个不重复的微秒，具备自动校正时间功能，例如启动时时间是错误的，待系统自动同步时间后，此处会自动同步，偏差超过CALIBRATION_MILLIS时才会校正，校正延迟2秒以内
     * @return
     */
    public synchronized long nowMicros() {
        long current =  startMicrosTime + (System.nanoTime() / 1000 - startMicros);
        if(checkTime) {// 自动校准时间
            checkTime = false;
            long millis = System.currentTimeMillis();
            if(Math.abs(millis - (current / 1000)) > CALIBRATION_MILLIS) {
                init();
                current =  startMicrosTime + (System.nanoTime() / 1000 - startMicros);
            }
        }
        if(current < previous) {
            return -1;
        }
        if(current == previous) {
            return nowMicros();
        }
        previous = current;
        return current;
    }

    @Override
    public void run() {
        checkTime = true;
    }

    public static void main(String[] args) {
        MicrosClock clock = new MicrosClock();
        int threadNum = 5;
//        AtomicLong number = new AtomicLong();
        Thread[] ts = new Thread[threadNum];
        for(int i = 0; i < threadNum; i++) {
            ts[i] = new Thread(() -> {
                for(int j = 0; j < 1000000000L; j++) {
//                    System.err.println(clock.nowMicros() + "---" + Thread.currentThread().getName());
//                    System.err.println(Thread.currentThread().getName() + "---" + clock.nowMicros());
                    clock.nowMicros();
                }
            });
        }
        for(Thread t : ts) {
            t.start();
        }
    }

}
