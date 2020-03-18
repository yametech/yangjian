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
package com.yametech.yangjian.agent.core.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import com.yametech.yangjian.agent.core.YMAgent;
import com.yametech.yangjian.agent.core.exception.AgentPathException;

/**
 * @author zcn
 * @date: 2019-10-16
 *
 **/
public class AgentPath {
    private static File file;

    /**
     *   获取agent根目录，子目录为：config、lib、logs、plugins
     */
    public static File getPath(){
        if(file != null) return file;
//        System.err.println(YMAgent.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String classPath = YMAgent.class.getName().replaceAll("\\.", "/") + ".class";
        URL resource = ClassLoader.getSystemClassLoader().getResource(classPath);
        String path = resource.toString();

        int insidePathIndex = path.indexOf('!');
        boolean isInJar = insidePathIndex > -1;
        if(isInJar){
            path = path.substring(path.indexOf("file:"), insidePathIndex);
            try {
                return file = new File(new URL(path).toURI()).getParentFile().getParentFile();
            } catch (URISyntaxException | MalformedURLException e) {
                throw new AgentPathException("Fail to parse agent path");
            }
        }else{
            String location = path.substring("file:".length(), path.length() - classPath.length());
            return file = new File(location).getParentFile();
        }
    }

}
