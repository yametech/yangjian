/**
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

package com.yametech.yangjian.agent.benchmark.combine;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import redis.clients.jedis.Jedis;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * @author dengliming
 * @date 2020/2/10
 */
@BenchmarkMode({Mode.All})// Throughput:整体吞吐量（每秒可以调用次数） AverageTime: 每次调用平均耗时
@Warmup(iterations = 1)// 预热（为了结果更加接近真实情况）
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)// iterations:测试轮次 time:每轮进行的时长 timeUnit:时长单位
@Threads(1)// 测试的线程数 一般为cpu*2
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 输出结果的时间粒度
@State(Scope.Benchmark)
public class CombineBenchmark {

    private DataSource dataSource;
    private Jedis jedis;

    /**
     * benchmark之前执行初始化
     */
    @Setup
    public void before() {
        jedis = new Jedis("127.0.0.1", 6379);
        HikariConfig config = new HikariConfig();
        //Class.forName("com.mysql.cj.jdbc.Driver");
        //config.setDriverClassName("com.mysql.jdbc.Driver");
        //config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        //config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true");
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai");
        config.setUsername("root");
        config.setPassword("123456");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "256");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(500);
        dataSource = new HikariDataSource(config);
    }

    /**
     * benchmark之后执行
     */
    @TearDown
    public void after() {
        ((HikariDataSource) dataSource).close();
    }

    /*@Benchmark
    public void test() {

    }*/

    @Benchmark
    public Object test() throws SQLException {
        String name = jedis.get("1");
        if (name == null) {
            Connection connection = null;
            PreparedStatement statement = null;
            try {
                connection = dataSource.getConnection();
                //SQL语句已发送给数据库，并编译好为执行作好准备
                statement = connection.prepareStatement("select * from user where id=?");
                //对占位符进行初始化
                statement.setString(1, "1");
                //statement.setInt(2,1001);
                //执行SQL语句
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    name = resultSet.getString("name");
                    jedis.set("1", name);
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
        }
        return name;
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
                .include(CombineBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}

/**

 Benchmark                             Mode  Cnt   Score    Error   Units
 CombineBenchmark.test                thrpt    5   0.032 ±  0.001  ops/ms
 CombineBenchmark.test                 avgt    5  31.483 ±  0.318   ms/op
 CombineBenchmark.test               sample  798  31.415 ±  0.076   ms/op
 CombineBenchmark.test:test·p0.00    sample       30.015            ms/op
 CombineBenchmark.test:test·p0.50    sample       31.326            ms/op
 CombineBenchmark.test:test·p0.90    sample       32.244            ms/op
 CombineBenchmark.test:test·p0.95    sample       32.639            ms/op
 CombineBenchmark.test:test·p0.99    sample       32.899            ms/op
 CombineBenchmark.test:test·p0.999   sample       33.227            ms/op
 CombineBenchmark.test:test·p0.9999  sample       33.227            ms/op
 CombineBenchmark.test:test·p1.00    sample       33.227            ms/op
 CombineBenchmark.test                   ss    5  31.427 ±  1.359   ms/op

with agent

 Benchmark                             Mode  Cnt   Score    Error   Units
 CombineBenchmark.test                thrpt    5   0.032 ±  0.001  ops/ms
 CombineBenchmark.test                 avgt    5  31.413 ±  0.127   ms/op
 CombineBenchmark.test               sample  795  31.511 ±  0.083   ms/op
 CombineBenchmark.test:test·p0.00    sample       30.081            ms/op
 CombineBenchmark.test:test·p0.50    sample       31.457            ms/op
 CombineBenchmark.test:test·p0.90    sample       32.309            ms/op
 CombineBenchmark.test:test·p0.95    sample       32.742            ms/op
 CombineBenchmark.test:test·p0.99    sample       32.965            ms/op
 CombineBenchmark.test:test·p0.999   sample       39.191            ms/op
 CombineBenchmark.test:test·p0.9999  sample       39.191            ms/op
 CombineBenchmark.test:test·p1.00    sample       39.191            ms/op
 CombineBenchmark.test                   ss    5  43.827 ± 96.764   ms/op


 */
