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

import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.core.config.Config;
import com.yametech.yangjian.agent.core.log.appender.ConsoleAppender;
import com.yametech.yangjian.agent.core.log.appender.RollingFileAppender;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zcn
 * @author dengliming
 **/
public class AppenderFactory {
    /**
     * 默认根日志实现
     */
    public static final String ROOT_APPENDER = "ROOT";
    /**
     * 预留链路使用日志实现
     */
    public static final String TRACE_APPENDER = "TRACE";
    private static final Map<String, IAppender> APPENDER_REGISTRY = new HashMap<>();

    public static IAppender buildAppender() {
        return buildAppender(ROOT_APPENDER);
    }

    public static IAppender buildAppender(String appenderName) {
        if (!TRACE_APPENDER.equals(appenderName)) {
            appenderName = ROOT_APPENDER;
        }
        return buildAppender(appenderName, LogOutput.valueOf(Config.getKv(Constants.LOG_OUTPUT, LoggerFactory.DEFAULT_OUTPUT.name())));
    }

    public static IAppender buildAppender(String appenderName, LogOutput output) {
        if (appenderName == null) {
            appenderName = ROOT_APPENDER;
        }
        IAppender appender = APPENDER_REGISTRY.get(appenderName);
        if (appender != null) {
            return appender;
        }
        synchronized (AppenderFactory.class) {
            if (appender == null) {
                switch (output) {
                    case FILE:
                        appender = new RollingFileAppender(appenderName);
                        break;
                    case CONSOLE:
                        appender = new ConsoleAppender();
                        break;
                    default:
                        throw new IllegalStateException("don't support log output : " + output);
                }
                APPENDER_REGISTRY.put(appenderName, appender);
            }
        }
        return appender;
    }
}
