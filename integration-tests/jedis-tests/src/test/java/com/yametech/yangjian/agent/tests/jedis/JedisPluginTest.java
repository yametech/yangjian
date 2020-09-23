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

package com.yametech.yangjian.agent.tests.jedis;

import com.yametech.yangjian.agent.tests.tool.AbstractAgentTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.embedded.RedisServer;
import zipkin2.Span;

import java.time.Duration;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author dengliming
 * @date 2020/9/22
 */
public class JedisPluginTest extends AbstractAgentTest {

    private JedisPool pool;
    private RedisServer redisServer;

    @Before
    public void setUp() {
        redisServer = RedisServer.builder()
                // maxheap
                .setting("maxmemory 64m")
                .port(6379)
                .setting("bind localhost")
                .build();
        redisServer.start();
        pool = new JedisPool(initJedisPoolConfig(), "localhost", 6379);
    }

    @After
    public void tearDown() {
        pool.destroy();
        redisServer.stop();
    }

    @Test
    public void test1() {
        try (Jedis jedis = pool.getResource()) {
            jedis.select(0);
            String key1 = "TS:2", key2 = "test1";
            jedis.set(key1, "12");
            jedis.hset(key2, "k1", "13");
            jedis.get(key1);
        }
        System.out.println("=====================================================>>>>>>start");
        List<Span> spans = mockTracerServer.waitForSpans(3, Duration.ofSeconds(5).toMillis());
        System.out.println("=====================================================>>>>>>end");
        assertNotNull(spans);
        assertEquals(3, spans.size());
    }

    @Test
    public void testSet() {
        try (Jedis jedis = pool.getResource()) {
            jedis.select(0);
            jedis.sadd("set", "k1");
        }
        System.out.println("=====================================================>>>>>>start1");
        List<Span> spans = mockTracerServer.waitForSpans(1, Duration.ofSeconds(5).toMillis());
        System.out.println("=====================================================>>>>>>end1");
        assertNotNull(spans);
        assertEquals(1, spans.size());
    }

    @Test
    public void testList() {
        try (Jedis jedis = pool.getResource()) {
            jedis.select(0);
            jedis.lpush("list:1", "k1");
        }
        System.out.println("=====================================================>>>>>>start2");
        List<Span> spans = mockTracerServer.waitForSpans(1, Duration.ofSeconds(5).toMillis());
        System.out.println("=====================================================>>>>>>end2");
        assertNotNull(spans);
        assertEquals(1, spans.size());
    }

    private static JedisPoolConfig initJedisPoolConfig() {
        //设置连接池的相关配置
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(2);
        poolConfig.setMaxIdle(1);
        poolConfig.setMaxWaitMillis(2000);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        return poolConfig;
    }
}
