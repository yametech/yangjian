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
package com.yametech.yangjian.agent.plugin.jedis;

import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dengliming
 * @date 2020/7/16
 */
public class JedisSentinelTest {

    public static void main(String[] args) {

    }

    @Test
    public void t1() {
        Set<String> sentinels = new HashSet<>();
        sentinels.add(new HostAndPort("127.0.0.1", 26379).toString());
        sentinels.add(new HostAndPort("127.0.0.1", 26479).toString());
        sentinels.add(new HostAndPort("127.0.0.1", 26579).toString());
        JedisSentinelPool sentinelPool = new JedisSentinelPool("mymaster", sentinels);

        System.out.println("Master: " + sentinelPool.getCurrentHostMaster().toString());

        Jedis master = sentinelPool.getResource();
        master.set("test", "a");

        System.out.println(master.get("test"));

        sentinelPool.close();
        sentinelPool.destroy();
    }
}
