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

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

public class RedissonTest {

    @org.junit.Test
    public void test() {
        Config config = new Config();
        config.useSingleServer().setAddress("127.0.0.1:6379");
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
}
