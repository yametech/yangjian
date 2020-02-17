package redis.clients.jedis;

import cn.ecpark.tool.agent.plugin.spring.Executor;

public class Jedis {


    public Jedis(final String host) {

    }

    public Jedis(final String host, final int port) {

    }

    public String set(final String key, String value) {
        Executor.Instance().doExecute();
        return value;
    }

    public String get(final String key) {
        Executor.Instance().doExecute();
        return null;
    }

    public void close() {

    }
}
