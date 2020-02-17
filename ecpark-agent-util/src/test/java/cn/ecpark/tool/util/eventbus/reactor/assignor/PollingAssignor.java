package cn.ecpark.tool.util.eventbus.reactor.assignor;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 轮询分配
 * @param <T>
 */
public class PollingAssignor<T> implements MultiThreadAssignor<T> {
    private AtomicLong counter = new AtomicLong(Long.MIN_VALUE);

    @Override
    public int threadNum(T msg, int totalThreadNum) {
        return (int)(Math.abs(counter.getAndIncrement()) % totalThreadNum);
    }
}
