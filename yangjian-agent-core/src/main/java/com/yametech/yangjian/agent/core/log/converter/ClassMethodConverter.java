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
package com.yametech.yangjian.agent.core.log.converter;

import com.yametech.yangjian.agent.core.log.IConverter;
import com.yametech.yangjian.agent.core.log.LogEvent;

/**
 * @author zcn
 * @date: 2019-10-14
 **/
public class ClassMethodConverter implements IConverter<LogEvent> {

    @Override
    public String convert(LogEvent event) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement found = null;
        for (int i = 0, len = stackTraceElements.length; i < len; i++) {
            StackTraceElement ele = stackTraceElements[i];
            if (ele.getClassName().endsWith(event.getTargetClass())) {
                found = ele;
                break;
            }
        }

        // 以下是固定调用方法栈
        //java.lang.Thread.getStackTrace
        //com.yametech.yangjian.agent.core.log.converter.ClassMethodConverter.convert
        //com.yametech.yangjian.agent.core.log.converter.ClassMethodConverter.convert
        //com.yametech.yangjian.agent.core.log.impl.PatternLogger.log
        //com.yametech.yangjian.agent.core.log.impl.PatternLogger.logIfEnabled
        //com.yametech.yangjian.agent.core.log.impl.PatternLogger.info
        //sourceclass.method
        //...
        if (found == null && stackTraceElements.length >= 7) {
            found = stackTraceElements[6];
        }

        return found == null ? event.getTargetClass() : new StringBuilder()
                .append(found == null ? event.getTargetClass() : cutClassName(found.getClassName()))
                .append(found.getMethodName())
                .append("(").append(found.getLineNumber()).append(")")
                .toString();
    }

    private static String cutClassName(String className) {
        String[] classes = className.split("\\.");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < classes.length; i++) {
            String c = classes[i];
            sb.append(i < classes.length - 1 ? c.charAt(0) : c).append(".");
        }
        return sb.toString();
    }
}
