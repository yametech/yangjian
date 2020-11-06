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
import com.yametech.yangjian.agent.tests.tool.bean.EventMetric;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
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

    private RedisServer redisServer;
    private Jedis jedis;

    @Before
    public void setUp() {
        redisServer = RedisServer.builder()
                // maxheap
                .setting("maxmemory 64m")
                .port(6379)
                .setting("bind localhost")
                .build();
        redisServer.start();
        jedis = new Jedis("localhost", 6379);
    }

    @After
    public void tearDown() {
        jedis.close();
        redisServer.stop();
    }

    @Test
    public void test1() {
        String key1 = "TS:2", key2 = "TS:1";
        jedis.set(key1, "12");
        jedis.hset(key2, "k1", "13");
        jedis.get(key1);
        List<Span> spans = mockTracerServer.waitForSpans(3, Duration.ofSeconds(5).toMillis());
        assertNotNull(spans);
        assertEquals(3, spans.size());

        List<EventMetric> metrics = mockMetricServer.waitForMetrics(1);
        assertEquals(1, metrics.size());
        EventMetric metric = metrics.get(0);
        assertEquals("TS", metric.getSign());
        assertEquals(3, metric.getNum());
        assertEquals("redis-key", metric.getType());
    }

    @Test
    public void testSet() {
        jedis.sadd("TS:SET", "k1");
        List<Span> spans = mockTracerServer.waitForSpans(1, Duration.ofSeconds(5).toMillis());
        assertNotNull(spans);
        assertEquals(1, spans.size());

        List<EventMetric> metrics = mockMetricServer.waitForMetrics(1);
        assertEquals(1, metrics.size());
        EventMetric metric = metrics.get(0);
        assertEquals("TS", metric.getSign());
        assertEquals(1, metric.getNum());
        assertEquals("redis-key", metric.getType());
    }

    @Test
    public void testList() {
        jedis.lpush("TS:LIST:1", "k1");
        List<Span> spans = mockTracerServer.waitForSpans(1, Duration.ofSeconds(5).toMillis());
        assertNotNull(spans);
        assertEquals(1, spans.size());
        List<EventMetric> metrics = mockMetricServer.waitForMetrics(1);
        assertEquals(1, metrics.size());
    }
}
