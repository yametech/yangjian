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
package com.yametech.yangjian.agent.core.jvm.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

/**
 * @author zcn
 * @date: 2019-10-18
 **/
public class CommandExecutor {

    private final static ILogger logger = LoggerFactory.getLogger(CommandExecutor.class);

    public CommandResult execute(String[] commands){
        try {
            Process process = Runtime.getRuntime().exec(commands);
            return acquire(process);
        }catch (IOException e) {
            logger.error(e, "Fail to execute command , command: {}");
            return new CommandResult(false, e.getLocalizedMessage().getBytes());
        }
    }

    private CommandResult acquire(Process process) throws IOException{
        try {
            byte[] error = readStream(process.getErrorStream());
            if(error.length != 0){
                return new CommandResult(false, error);
            }

            byte[] content = readStream(process.getInputStream());
            return new CommandResult(true, content);
        } finally {
            process.getInputStream().close();
            process.getOutputStream().close();
            process.getErrorStream().close();
            process.destroy();
        }
    }

    private byte[] readStream(InputStream inputStream) throws IOException{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int bufSize = 1024;
        byte[] buff = new byte[bufSize];
        int len = 0;
        while( (len = inputStream.read(buff, 0 , bufSize)) != -1){
            outputStream.write(buff,0, len);
        }
        return outputStream.toByteArray();
    }

}
