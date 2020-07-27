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

import redis.clients.jedis.*;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2019/12/4
 */
public class JedisSingleTest {

    public static void main(String[] args) {
        //t();
        //t1();
        t();
    }

    public static void t() {
        //创建连接池配置对象
        JedisPoolConfig config = initJedisPoolConfig();
        try (JedisPool pool = new JedisPool(config, "127.0.0.1", 6379);
             Jedis jedis = pool.getResource()) {
            //登录，如果没有设置密码这段可以省略
            //jedis.auth("1234");
            //选择DB0数据库
            jedis.select(0);
            //Set<String> keyList = jedis.keys("*");
            String key1 = "TS:2", key2 = "test1";
            //none(key不存在),string(字符串),list(列表),set(集合),zset(有序集),hash(哈希表)
            //String type = jedis.type(key1);
            //jedis.hset(key2, "a","12");
            jedis.set(key1.getBytes(), "12".getBytes());
            jedis.set(key1.getBytes(), "13".getBytes());
            System.out.println("====================================>>>>>>>" + jedis.get(key1));
        }
    }

    public static void t1() {
        //设置连接池的相关配置
        JedisPoolConfig poolConfig = initJedisPoolConfig();
        JedisShardInfo shardInfo1 = new JedisShardInfo("127.0.0.1", 6379, 5000);
        ShardedJedisPool jedisPool = new ShardedJedisPool(poolConfig, Arrays.asList(shardInfo1));
        //进行查询等其他操作
        ShardedJedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set("test", "test");
            jedis.set("test1", "test1");
            jedis.set("test2", "test2");
            jedis.set("test3", "test3");
            jedis.set("test4", "test4");

            //查看具体key在哪个客户端
            Client client0 = jedis.getShard("test").getClient();
            Client client1 = jedis.getShard("test1").getClient();
            Client client2 = jedis.getShard("test2").getClient();
            Client client3 = jedis.getShard("test3").getClient();
            Client client4 = jedis.getShard("test4").getClient();
            System.out.println(client0.getHost() + ":" + client0.getPort());
            System.out.println(client1.getHost() + ":" + client1.getPort());
            System.out.println(client2.getHost() + ":" + client2.getPort());
            System.out.println(client3.getHost() + ":" + client3.getPort());
            System.out.println(client4.getHost() + ":" + client4.getPort());
        } finally {
            //使用后一定关闭，还给连接池
            if (jedis != null) {
                jedis.close();
            }
        }
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
