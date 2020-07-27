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
package com.yametech.yangjian.agent.plugin.redisson;

import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

public class RedissonTest {

    /**
     * 单节点测试
     */
    @org.junit.Test
    public void testSingleServer() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);
        RBucket<Object> result = redisson.getBucket("key3");
        result.set("gg", 5, TimeUnit.MINUTES);
        System.err.println(result.get());
        RSet r = redisson.getSet("key5");
        System.err.println(r.size());
        RLock rLock = redisson.getLock("tlock");
        try {
            rLock.tryLock(1, 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 集群测试
     */
    @org.junit.Test
    public void testClusterServer() {
        //创建配置
        Config config = new Config();
        //指定使用集群部署方式
        config.useClusterServers()
                // 集群状态扫描间隔时间，单位是毫秒
                .setScanInterval(2000)
                //cluster方式至少6个节点(3主3从，3主做sharding，3从用来保证主宕机后可以高可用)
                .addNodeAddress("redis://127.0.0.1:6382")
                .addNodeAddress("redis://127.0.0.1:6383")
                .addNodeAddress("redis://127.0.0.1:6384");
        //创建客户端(发现这一非常耗时，基本在2秒-4秒左右)
        RedissonClient redisson = Redisson.create(config);

        RBucket<String> keyObject = redisson.getBucket("key");
        keyObject.set("value");

        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        redisson.shutdown();
    }

    /**
     * 哨兵测试
     */
    @Test
    public void testSentinel() {
        //创建配置
        Config config = new Config();
        config.useSentinelServers()
                .setMasterName("mymaster")
                .addSentinelAddress("redis://127.0.0.1:26379")
                .addSentinelAddress("redis://127.0.0.1:26479")
                .addSentinelAddress("redis://127.0.0.1:26579");

        RedissonClient redisson = Redisson.create(config);
        RBucket<String> keyObject = redisson.getBucket("key");
        keyObject.set("value");
        redisson.shutdown();
    }

    @Test
    public void testMasterSlave() {
        Config config = new Config();
        //指定使用主从部署方式
        config.useMasterSlaveServers()
                //设置redis主节点
                .setMasterAddress("redis://127.0.0.1:6379")
                //设置redis从节点
                .addSlaveAddress("redis://127.0.0.1:6380", "redis://127.0.0.1:6381");
        RedissonClient redisson = Redisson.create(config);

        RBucket<String> keyObject = redisson.getBucket("key");
        keyObject.set("value");
        redisson.shutdown();
    }
}
