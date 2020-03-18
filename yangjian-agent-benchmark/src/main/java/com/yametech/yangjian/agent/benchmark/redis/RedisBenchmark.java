/*
 * Copyright 2020 yametech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yametech.yangjian.agent.benchmark.redis;

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
        Jedis jedis = pool.getResource();
        jedis.get("key1");
        jedis.close();
    }

    /**
     * 探针测试
     * -javaagent:D:\workspace\yangjian\deploy\lib\ecpark-agent.jar -Dservice.name=test
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
