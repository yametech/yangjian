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
package com.yametech.yangjian.agent.benchmark.httpclient;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author dengliming
 * @date 2020/2/6
 */
@BenchmarkMode({Mode.All})// Throughput:整体吞吐量（每秒可以调用次数） AverageTime: 每次调用平均耗时
@Warmup(iterations = 1)// 预热（为了结果更加接近真实情况）
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)// iterations:测试轮次 time:每轮进行的时长 timeUnit:时长单位
@Threads(1)// 测试的线程数 一般为cpu*2
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 输出结果的时间粒度
@State(Scope.Benchmark)
public class HttpClientBenchmark {

    private CloseableHttpClient client = HttpClients.createDefault();

    /**
     * benchmark之前执行初始化
     */
    @Setup
    public void before() {
    }

    /**
     * benchmark之后执行
     */
    @TearDown
    public void after() {
    }

    @Benchmark
    public void test() {
        // 创建http GET请求
        HttpGet httpGet = new HttpGet("http://whois.pconline.com.cn/?ip=117.89.35.98");
        //HttpGet httpGet = new HttpGet("http://127.0.0.1:8080/ping");
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = client.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
                .include(HttpClientBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}
/**

 Benchmark                                Mode  Cnt     Score    Error   Units
 HttpClientBenchmark.test                thrpt    5     0.020 ±  0.002  ops/ms
 HttpClientBenchmark.test                 avgt    5    54.181 ± 35.930   ms/op
 HttpClientBenchmark.test               sample  636    80.132 ± 18.701   ms/op
 HttpClientBenchmark.test:test·p0.00    sample         37.618            ms/op
 HttpClientBenchmark.test:test·p0.50    sample         54.985            ms/op
 HttpClientBenchmark.test:test·p0.90    sample         64.651            ms/op
 HttpClientBenchmark.test:test·p0.95    sample         98.252            ms/op
 HttpClientBenchmark.test:test·p0.99    sample        954.089            ms/op
 HttpClientBenchmark.test:test·p0.999   sample       1595.933            ms/op
 HttpClientBenchmark.test:test·p0.9999  sample       1595.933            ms/op
 HttpClientBenchmark.test:test·p1.00    sample       1595.933            ms/op
 HttpClientBenchmark.test                   ss    5    97.368 ± 56.055   ms/op

 Process finished with exit code 0




 */
