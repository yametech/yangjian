package cn.ecpark.tool.agent.plugin.jedis;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.TimeUnit;

/**
 * @author dengliming
 * @date 2020/2/6
 */
@BenchmarkMode({Mode.Throughput})// Throughput:整体吞吐量（如每秒可以调用次数） AverageTime: 每次调用平均耗时
@Warmup(iterations = 1)// 预热（为了结果更加接近真实情况）
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.MINUTES)// iterations:测试轮次 time:每轮进行的时长 timeUnit:时长单位
@Threads(1)// 测试的线程数 一般为cpu*2
@Fork(1)
@OutputTimeUnit(TimeUnit.SECONDS) // 输出结果的时间粒度
@State(Scope.Benchmark)
public class RedisBenchmark {

    private JedisPool pool = null;

    /**
     * benchmark之前执行初始化
     */
    @Setup
    public void before() {
        //设置连接池的相关配置
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(20);
        poolConfig.setMaxIdle(1);
        poolConfig.setMaxWaitMillis(2000);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        pool = new JedisPool(poolConfig, "127.0.0.1", 6379);
    }

    /**
     * benchmark之后执行
     */
    @TearDown
    public void after() {
        pool.close();
    }

    @Benchmark
    public void test() {
        try (Jedis jedis = pool.getResource()) {
            jedis.select(0);
            jedis.get("key1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 探针测试
     * -javaagent:D:\workspace\g-server\tool-ecpark-agent\ecpark-agent-core\dist\ecpark-agent\lib\ecpark-agent.jar=args -Dskywalking.agent.service_name=test -Dservice.name=test
     *
     * @param args
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(RedisBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}