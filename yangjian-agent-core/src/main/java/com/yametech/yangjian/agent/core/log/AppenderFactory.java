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

import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.core.config.Config;
import com.yametech.yangjian.agent.core.log.impl.ConsoleAppender;
import com.yametech.yangjian.agent.core.log.impl.RollingFileAppender;

/**
 * @author zcn
 * @date: 2019-10-16
 **/
public class AppenderFactory {

    private static Appender appender = null;

    public static Appender buildAppender(){
        if(appender != null) {
            return appender;
        }else{
            synchronized (AppenderFactory.class){
                if(appender != null){
                    return appender;
                }
                LogOutput output = LogOutput.valueOf(Config.getKv(Constants.LOG_OUTPUT, LoggerFactory.DEFAULT_OUTPUT.name()));
                switch (output){
                    case FILE:
                        return appender =  new RollingFileAppender();
                    case CONSOLE:
                        return appender =  new ConsoleAppender();
                    default:
                        throw new IllegalStateException("don't support log output : " + output);
                }
            }
        }
    }

}
