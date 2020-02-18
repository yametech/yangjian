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

package com.yametech.yangjian.agent.core.log.converter;

import com.yametech.yangjian.agent.core.log.Converter;
import com.yametech.yangjian.agent.core.log.LogEvent;

/**
 * @author zcn
 * @date: 2019-10-14
 **/
public class ClassMethodConverter implements Converter {
    @Override
    public String convert(LogEvent event) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        StackTraceElement found = null;
        for(int i = 0, len = stackTraceElements.length; i < len - 1; i++){
            StackTraceElement ele = stackTraceElements[i];
            if(ele.getClassName().endsWith(event.getTargetClass())){
                found = ele;
                break;
            }
        }

        if(found == null){
            found = stackTraceElements[5];
        }

        return new StringBuilder()
                .append(cutClassName(found.getClassName()))
                .append(found.getMethodName())
                .append("(").append(found.getLineNumber()).append(")")
                .toString();
    }

    private static String cutClassName(String className){
        String[] classes = className.split("\\.");

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < classes.length; i++){
            String c = classes[i];
            sb.append(i < classes.length - 1 ? c.charAt(0) : c).append(".");
        }
        return sb.toString();
    }

}
