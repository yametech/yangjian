package cn.ecpark.tool.agent.plugin.kafka;

import cn.ecpark.plugin.mq.producer.Publisher;
import cn.ecpark.plugin.mq.producer.config.KafkaPublishConfig;
import cn.ecpark.plugin.mq.serialization.JsonSerializer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author dengliming
 * @date 2020/2/9
 */
@BenchmarkMode({Mode.All})// Throughput:整体吞吐量（每秒可以调用次数） AverageTime: 每次调用平均耗时
@Warmup(iterations = 1)// 预热（为了结果更加接近真实情况）
@Measurement(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)// iterations:测试轮次 time:每轮进行的时长 timeUnit:时长单位
@Threads(1)// 测试的线程数 一般为cpu*2
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 输出结果的时间粒度
@State(Scope.Benchmark)
public class KafkaPublishBenchmark {
    private static final String ADDRESS = "10.200.10.19:9094,10.1.1.232:9094,10.1.1.190:9094";
    private static final String USER_NAME = "";
    private static final String PASS = "";
    private static final String TOPIC = "ECPARK-AGENT-TOPIC";
    private static final int PARTITION_NUM = 1;
    private Publisher publisher;

    /**
     * benchmark之前执行初始化
     */
    @Setup
    public void before() {
        KafkaPublishConfig config = new KafkaPublishConfig();
        config.setAppName("test-client-liuzz");
        config.setServerAddress(ADDRESS);
        config.setUserName(USER_NAME);
        config.setPass(PASS);
        config.setSerializer(new JsonSerializer(Map.class));
        config.setTopic(TOPIC);
        config.setShardTopicNum(1);
        config.setNumPartitions(PARTITION_NUM);
        config.setReplicationFactor((short) 1);
//		config.setCompressionType("lz4");
        config.setSendAsync(true);
        publisher = new Publisher(config);
    }

    /**
     * benchmark之后执行
     */
    @TearDown
    public void after() {
        publisher.shutdown();
    }

    @Benchmark
    public Object test() {
        Map<String, Boolean> data = new HashMap<>();
        data.put("kafka", true);
        publisher.publish("test", data);
        return true;
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
                .include(KafkaPublishBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}

/**






 Benchmark                                  Mode     Cnt    Score    Error   Units
 KafkaPublishBenchmark.test                thrpt       5   62.388 ± 11.072  ops/ms
 KafkaPublishBenchmark.test                 avgt       5    0.019 ±  0.047   ms/op
 KafkaPublishBenchmark.test               sample  265826    0.020 ±  0.009   ms/op
 KafkaPublishBenchmark.test:test·p0.00    sample            0.001            ms/op
 KafkaPublishBenchmark.test:test·p0.50    sample            0.014            ms/op
 KafkaPublishBenchmark.test:test·p0.90    sample            0.027            ms/op
 KafkaPublishBenchmark.test:test·p0.95    sample            0.028            ms/op
 KafkaPublishBenchmark.test:test·p0.99    sample            0.040            ms/op
 KafkaPublishBenchmark.test:test·p0.999   sample            0.158            ms/op
 KafkaPublishBenchmark.test:test·p0.9999  sample            0.268            ms/op
 KafkaPublishBenchmark.test:test·p1.00    sample          321.913            ms/op
 KafkaPublishBenchmark.test                   ss       5    0.743 ±  5.069   ms/op

 */