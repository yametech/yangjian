package cn.ecpark.tool.javaagent.config;

import java.util.concurrent.TimeUnit;

import cn.ecpark.tool.agent.core.config.RemoteConfigReader;

/**
 * @author dengliming
 * @date 2019/12/2
 */
public class RemoteConfigTest {

    public static void main(String[] args) {
        t();
    }

    public static void t() {
        RemoteConfigReader remoteConfigReader = new RemoteConfigReader();
        remoteConfigReader.load(null);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
