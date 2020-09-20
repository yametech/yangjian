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
package com.yametech.yangjian.agent.plugin.lettuce;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.masterslave.MasterSlave;
import io.lettuce.core.masterslave.StatefulRedisMasterSlaveConnection;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LettuceTest {

    @org.junit.Test
    public void test() {

    }

    public static void main(String[] args) throws Exception {
    }

    @Test
    public void testCluster() throws InterruptedException {
        // Syntax: redis://[password@]host[:port]
        RedisClusterClient redisClusterClient = RedisClusterClient.create("redis://localhost:6382,localhost:6383,localhost:6384");

        // 定时更新集群拓扑视图
        ClusterTopologyRefreshOptions options = ClusterTopologyRefreshOptions
                .builder()
                .enablePeriodicRefresh(Duration.of(5, ChronoUnit.SECONDS))
                .build();
        redisClusterClient.setOptions(ClusterClientOptions.builder().topologyRefreshOptions(options).build());


        StatefulRedisClusterConnection<String, String> connection = redisClusterClient.connect();

        System.out.println("Connected to Redis");
        RedisAdvancedClusterCommands<String, String> redisCommands = connection.sync();
        redisCommands.set("test1", "13");
        System.out.println(redisCommands.get("test1"));
        TimeUnit.SECONDS.sleep(60);

        connection.close();
        redisClusterClient.shutdown();
    }

    @Test
    public void testSentinels() {
        // Syntax: redis-sentinel://[password@]host[:port][,host2[:port2]][/databaseNumber]#sentinelMasterId
        RedisClient redisClient = RedisClient.create("redis-sentinel://localhost:26379,localhost:26479,localhost:26579/0#mymaster");

        StatefulRedisConnection<String, String> connection = redisClient.connect();

        System.out.println("Connected to Redis using Redis Sentinel");
        RedisCommands redisCommands = connection.sync();
        redisCommands.set("test1", "13");
        System.out.println(redisCommands.get("test1"));

        connection.close();
        redisClient.shutdown();
    }

    @Test
    public void testSingleServer() {
        RedisURI redisUri = RedisURI.builder()                    // <1> 创建单机连接的连接信息
                .withHost("localhost")
                .withPort(6379)
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        RedisClient redisClient = RedisClient.create(redisUri);   // <2> 创建客户端
        StatefulRedisConnection<String, String> connection = redisClient.connect();     // <3> 创建线程安全的连接
        RedisCommands<String, String> redisCommands = connection.sync();                // <4> 创建同步命令
        SetArgs setArgs = SetArgs.Builder.nx().ex(5);
        String result = redisCommands.set("name", "throwable", setArgs);
        System.out.println("=================>result1:" + result);
        result = redisCommands.get("name");
        System.out.println("=================>result2:" + result);
        // ... 其他操作
        connection.close();   // <5> 关闭连接
        redisClient.shutdown();  // <6> 关闭客户端
    }

    @Test
    public void testMasterSlave() throws InterruptedException {
        // Syntax: redis://[password@]host[:port][/databaseNumber]
        RedisClient redisClient = RedisClient.create();
        List<RedisURI> nodes = Arrays.asList(
                RedisURI.create("redis://127.0.0.1:6379"),
                RedisURI.create("redis://127.0.0.1:6380"),
                RedisURI.create("redis://127.0.0.1:6381"));
        StatefulRedisMasterSlaveConnection<String, String> connection = MasterSlave
                .connect(redisClient, StringCodec.UTF8, nodes);
        connection.setReadFrom(ReadFrom.MASTER_PREFERRED);
        System.out.println("Connected to Redis");

        RedisCommands redisCommands = connection.sync();
        redisCommands.set("test1", "13");
        System.out.println(redisCommands.get("test1"));
        TimeUnit.SECONDS.sleep(60);
        connection.close();
        redisClient.shutdown();
    }

}
