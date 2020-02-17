package cn.ecpark.tool.javaagent.log;

import org.junit.BeforeClass;
import org.junit.Test;

import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.core.config.Config;
import cn.ecpark.tool.agent.core.log.LogLevel;
import cn.ecpark.tool.agent.core.log.LogOutput;
import cn.ecpark.tool.agent.core.log.impl.PatternLogger;

/**
 * @author zcn
 * @date: 2019-10-15
 **/
public class PatternLoggerTest {

    private String pattern = "%timestamp [%level]-[%thread]-[%class.method]: %msg %throwable";

    @BeforeClass
    public static void beforeClass(){
        Config.setConfig(Constants.LOG_LEVEL, LogLevel.DEBUG.name());
        Config.setConfig(Constants.LOG_OUTPUT, LogOutput.CONSOLE.name());
        Config.setConfig(Constants.LOG_MAX_FILE_SIZE, Integer.valueOf(1024 * 1024).toString());
    }


    @Test
    public void testLog(){
        PatternLogger logger = new PatternLogger(pattern, PatternLoggerTest.class);

        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.warn(new NullPointerException(), "ttt");

        logger.error("error");
        logger.error(new NullPointerException(), "ttt");

        waitOneTick();
    }

    private void waitOneTick(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testArguments(){
        PatternLogger logger = new PatternLogger(pattern, PatternLoggerTest.class);
        logger.debug("abc {} {}", "def", "ghi");

        waitOneTick();
    }

}
