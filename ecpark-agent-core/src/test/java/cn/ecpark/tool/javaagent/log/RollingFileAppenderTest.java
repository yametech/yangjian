package cn.ecpark.tool.javaagent.log;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.core.config.Config;
import cn.ecpark.tool.agent.core.log.LogEvent;
import cn.ecpark.tool.agent.core.log.LogLevel;
import cn.ecpark.tool.agent.core.log.LogOutput;
import cn.ecpark.tool.agent.core.log.impl.RollingFileAppender;

/**
 * @author zcn
 * @date: 2019-10-15
 **/
public class RollingFileAppenderTest {

    @BeforeClass
    public static void beforeClass(){
        Config.setConfig(Constants.LOG_DIR, "../tmp");
        Config.setConfig(Constants.LOG_LEVEL, LogLevel.DEBUG.name());
        Config.setConfig(Constants.LOG_OUTPUT, LogOutput.FILE.name());
        Config.setConfig(Config.SERVICE_NAME.getKey(), "test");
        Config.setConfig(Constants.LOG_MAX_FILE_SIZE, "10");
        Config.setConfig(Constants.LOG_MAX_FILE_NUM, "10");
    }

   // @AfterClass
    public static void afterClass(){
        deleteDir(new File(Config.getKv(Constants.LOG_DIR)));
    }

    @Test
    public void testWrite(){
        RollingFileAppender appender = new RollingFileAppender();
        LogEvent logEvent = new LogEvent(LogLevel.INFO, "ttt", null, RollingFileAppenderTest.class.getCanonicalName());

        for(int i = 0; i < 10; i++){
            appender.append(logEvent);
        }

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                deleteDir(new File(dir, children[i]));
            }
        }
        dir.delete();
    }

}
