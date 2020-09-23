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
package com.yametech.yangjian.agent.core.core.classloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.yametech.yangjian.agent.api.base.SPI;
import com.yametech.yangjian.agent.api.common.InstanceManage;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

public class SpiLoader {
	private static final ILogger LOG = LoggerFactory.getLogger(SpiLoader.class);
	private static final String SPI_BASE_PATH = "META-INF/services/";

	private SpiLoader() {}

	/**
	 * 读取所有的spi class
	 */
 	public static void loadSpi() {
		List<String> spiClasses = getSpiClass(SPI.class);
		if(spiClasses == null) {
			return;
		}
		spiClasses.forEach(clsName -> {
			try {
				Class<?> cls = Class.forName(clsName, false, AgentClassLoader.getDefault());
				if(!SPI.class.isAssignableFrom(cls)) {
					return;
				}
				InstanceManage.addSpi(cls);
			} catch (ClassNotFoundException e) {
				LOG.warn(e, "load spi error");
			}
		});
    }

	public static List<String> getSpiClass(Class<?> cls) {
        List<String> spiClasses = new ArrayList<>();
        try {
        	Enumeration<URL> urls = AgentClassLoader.getDefault().getResources(SPI_BASE_PATH + cls.getName());
            while (urls.hasMoreElements()) {
            	URL url = urls.nextElement();
            	/*if("file".equals(url.getProtocol())) {// 开发环境，在ecpark-agent调试时会重复加载，所以使用protocol过滤
            		continue;
            	}*/
            	try (InputStream input = url.openStream()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String pluginDefine = null;
                    while ((pluginDefine = reader.readLine()) != null) {
                        if (pluginDefine.trim().length() == 0 || pluginDefine.startsWith("#")) {
                            continue;
                        }
                        spiClasses.add(pluginDefine);
                    }
                }
            }
            return spiClasses;
        } catch (IOException e) {
        	LOG.error("read resources failure.", e);
        }
        return null;
    }
}
