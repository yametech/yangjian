package cn.ecpark.tool.agent.plugin.redisson;

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
        RSet r = redisson.getSet("key5");
        r.size();
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
