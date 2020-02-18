/**
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
import com.yametech.yangjian.agent.core.log.impl.ConsoleAppender;

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
