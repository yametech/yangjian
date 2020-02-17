package cn.ecpark.tool.agent.core.util;

/**
 * @author dengliming
 * @date 2019/11/25
 */
public interface IRateLimiter {

    boolean tryAcquire();

    void release();
}
