package com.yametech.yangjian.agent.api.common;

public class MicrosClock {
    private static final int MICROS_MAX = 999;
    private long previousMillis;
    private int micros;

    public synchronized long nowMicros() {
        long nowMillis = System.currentTimeMillis();
        if(nowMillis < previousMillis) {// 时钟回退了
            return -1;
        }
        if(nowMillis == previousMillis) {
            int thisMicros = micros++;
            if(thisMicros > MICROS_MAX) {
                return nowMicros();
            }
            return nowMillis * 1000 + thisMicros;
        }
        previousMillis = nowMillis;
        micros = 0;
        return nowMillis * 1000 + micros++;
    }

    public static void main(String[] args) {
        MicrosClock clock = new MicrosClock();
        int threadNum = 10;
        Thread[] ts = new Thread[threadNum];
        for(int i = 0; i < threadNum; i++) {
            ts[i] = new Thread(() -> {
                for(int j = 0; j < 100; j++) {
                    System.err.println(clock.nowMicros());
                }
            });
        }
        for(Thread t : ts) {
            t.start();
        }
    }

}
