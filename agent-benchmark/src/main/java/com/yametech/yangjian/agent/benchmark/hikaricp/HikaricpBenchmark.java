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
package com.yametech.yangjian.agent.benchmark.hikaricp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * @author dengliming
 * @date 2020/2/6
 */
@BenchmarkMode({Mode.All})// Throughput:整体吞吐量（每秒可以调用次数） AverageTime: 每次调用平均耗时
@Warmup(iterations = 1)// 预热（为了结果更加接近真实情况）
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)// iterations:测试轮次 time:每轮进行的时长 timeUnit:时长单位
@Threads(1)// 测试的线程数 一般为cpu*2
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 输出结果的时间粒度
@State(Scope.Benchmark)
public class HikaricpBenchmark {

    private DataSource dataSource;

    /**
     * benchmark之前执行初始化
     */
    @Setup
    public void before() {
        HikariConfig config = new HikariConfig();
        //Class.forName("com.mysql.cj.jdbc.Driver");
        //config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        //config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true");
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai");
        config.setUsername("root");
        config.setPassword("123456");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "256");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(50);
        dataSource = new HikariDataSource(config);
    }

    /**
     * benchmark之后执行
     */
    @TearDown
    public void after() {
        ((HikariDataSource) dataSource).close();
    }

    @Benchmark
    public String test() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            //SQL语句已发送给数据库，并编译好为执行作好准备
            statement = connection.prepareStatement("select * from user where name=?");
            //对占位符进行初始化
            statement.setString(1, "liuzhao");
            //执行SQL语句
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return null;
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
                .include(HikaricpBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}
/**
 Benchmark                              Mode     Cnt   Score    Error   Units
 HikaricpBenchmark.test                thrpt       5  12.062 ±  0.578  ops/ms
 HikaricpBenchmark.test                 avgt       5   0.082 ±  0.001   ms/op
 HikaricpBenchmark.test               sample  608123   0.082 ±  0.001   ms/op
 HikaricpBenchmark.test:test·p0.00    sample           0.066            ms/op
 HikaricpBenchmark.test:test·p0.50    sample           0.079            ms/op
 HikaricpBenchmark.test:test·p0.90    sample           0.091            ms/op
 HikaricpBenchmark.test:test·p0.95    sample           0.109            ms/op
 HikaricpBenchmark.test:test·p0.99    sample           0.140            ms/op
 HikaricpBenchmark.test:test·p0.999   sample           0.176            ms/op
 HikaricpBenchmark.test:test·p0.9999  sample           0.734            ms/op
 HikaricpBenchmark.test:test·p1.00    sample           2.920            ms/op
 HikaricpBenchmark.test                   ss       5   1.258 ±  6.193   ms/op

 with Agent：


 Benchmark                              Mode     Cnt   Score    Error   Units
 HikaricpBenchmark.test                thrpt       5  11.527 ±  0.060  ops/ms
 HikaricpBenchmark.test                 avgt       5   0.087 ±  0.001   ms/op
 HikaricpBenchmark.test               sample  561746   0.089 ±  0.001   ms/op
 HikaricpBenchmark.test:test·p0.00    sample           0.074            ms/op
 HikaricpBenchmark.test:test·p0.50    sample           0.084            ms/op
 HikaricpBenchmark.test:test·p0.90    sample           0.100            ms/op
 HikaricpBenchmark.test:test·p0.95    sample           0.107            ms/op
 HikaricpBenchmark.test:test·p0.99    sample           0.131            ms/op
 HikaricpBenchmark.test:test·p0.999   sample           0.194            ms/op
 HikaricpBenchmark.test:test·p0.9999  sample           0.412            ms/op
 HikaricpBenchmark.test:test·p1.00    sample           3.572            ms/op
 HikaricpBenchmark.test                   ss       5   0.782 ±  0.699   ms/op


 */
