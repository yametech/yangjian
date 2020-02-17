package cn.ecpark.tool.agent.core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方法限流容器
 *
 * 使用方式：
 *  只需初始化一次：RateLimiterHolder.register(String groupKey, IRateLimiter rateLimiter);
 *  后面直接调用获取许可：RateLimiterHolder.tryAcquire(String groupKey);
 * @author dengliming
 * @date 2019/11/24
 */
public class RateLimiterHolder {

    private static Map<String, IRateLimiter> limiterMap = new ConcurrentHashMap<>();
    /**
     * 限流类型：日志打印速率控制
     */
    public static final String LOG_RATE_LIMIT_KEY = "LOG_RATE_LIMIT";

    static {
        // 可以直接初始化共用的
        //register(LOG_RATE_LIMIT_KEY, new SemaphoreLimiter(100, TimeUnit.SECONDS));
    }

    /**
     * 初始化注册到全局容器
     *
     * @param groupKey
     * @param rateLimiter
     */
    public static void register(String groupKey, IRateLimiter rateLimiter) {
        limiterMap.putIfAbsent(groupKey, rateLimiter);
    }

    private static IRateLimiter getRateLimiter(String groupKey) {
        return limiterMap.get(groupKey);
    }

    /**
     * 获取许可
     *
     * @param groupKey 以分组区分不同类型
     * @return
     */
    public static boolean tryAcquire(String groupKey) {
        IRateLimiter rateLimiter = getRateLimiter(groupKey);
        // 未定义直接返回可执行
        if (rateLimiter == null) {
            return true;
        }
        return rateLimiter.tryAcquire();
    }

    /**
     * 释放许可
     *
     * @param groupKey
     */
    public static void release(String groupKey) {
        IRateLimiter rateLimiter = getRateLimiter(groupKey);
        if (rateLimiter != null) {
            rateLimiter.release();
        }
    }
}
