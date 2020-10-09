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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import com.yametech.yangjian.agent.core.log.IConverter;
import com.yametech.yangjian.agent.core.log.LogEvent;

/**
 * @author zcn
 * @date: 2019-10-14
 */
public class ThrowableConverter implements IConverter<LogEvent> {

    private static String SEPARATOR = System.getProperty("line.separator", "\n");

    @Override
    public String convert(LogEvent event) {
        return event.getThrowable() == null ? "" : convert(event.getThrowable());
    }

    private String convert(Throwable throwable) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String traces = "";
        try {
            throwable.printStackTrace(new PrintWriter(output, true));
            traces = output.toString();
            output.close();
        } catch (IOException e) {
//            e.printStackTrace();// 此处不要打印log，导致死循环
        }
        return SEPARATOR + traces;
    }
}
