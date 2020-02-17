package cn.ecpark.tool.javaagent.log;

import org.junit.BeforeClass;
import org.junit.Test;

import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.core.config.Config;
import cn.ecpark.tool.agent.core.log.Appender;
import cn.ecpark.tool.agent.core.log.LogEvent;
import cn.ecpark.tool.agent.core.log.LogLevel;
import cn.ecpark.tool.agent.core.log.LogOutput;
import cn.ecpark.tool.agent.core.log.impl.ConsoleAppender;

/**
 * @author zcn
 * @date: 2019-10-15
 **/
public class ConsoleAppenderTest {

    @BeforeClass
    public static void beforeClass(){
        Config.setConfig(Constants.LOG_DIR, "../tmp");
        Config.setConfig(Constants.LOG_LEVEL, LogLevel.DEBUG.name());
        Config.setConfig(Constants.LOG_OUTPUT, LogOutput.CONSOLE.name());
        Config.setConfig(Config.SERVICE_NAME.getKey(), "test");
        Config.setConfig(Constants.LOG_MAX_FILE_SIZE, "10");
    }

    @Test
    public void testWrite(){
        Appender appender = new ConsoleAppender();
        LogEvent logEvent = new LogEvent(LogLevel.INFO, "ttt", null, ConsoleAppenderTest.class.getCanonicalName());

        for(int i = 0; i < 20; i++){
            appender.append(logEvent);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
