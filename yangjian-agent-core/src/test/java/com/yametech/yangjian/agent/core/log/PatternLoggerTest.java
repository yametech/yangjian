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
package com.yametech.yangjian.agent.core.log;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.core.config.Config;
import com.yametech.yangjian.agent.core.log.impl.PatternLogger;

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
