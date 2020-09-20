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
package com.yametech.yangjian.agent.core.config;

import com.yametech.yangjian.agent.api.IConfigLoader;
import com.yametech.yangjian.agent.api.common.Config;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.util.AgentPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * 加载本地配置，配置来源包含：jvm启动参数、指定路径配置文件、默认配置文件
 *
 * @author liuzhao
 * @Description
 * @date 2019年10月6日 下午10:23:16
 */
public class LocalConfigLoader implements IConfigLoader {
    private static ILogger log = LoggerFactory.getLogger(LocalConfigLoader.class);
    private static final String DEFAULT_CONFIG_FILE = "agent.properties";
	private static final String ENV_CONFIG_FILE_FORMAT = "agent-%s.properties";

    @Override
    public void load(String arguments) throws Exception {
        readLocalConfigFile();
        readEnvConfigFile();
        readSystemArguments();
        readArguments(arguments);
        readAgentVersion();
    }

    /**
     * 读取启动参数中的配置，如：-javaagent:...ecpark-agent.jar=arg=123,arg2=qqq，则此处的arguments为arg=123,arg2=qqq
     *
     * @param arguments
     */
    private void readArguments(String arguments) {
        if (arguments == null || arguments.trim().length() == 0) {
            return;
        }
        for (String arg : arguments.split(",")) {
            String[] kv = arg.trim().split("=", 2);
            if (kv.length == 0) {
                continue;
            }
            String key = kv[0].trim();
            String value = "";
            if (kv.length == 2) {
                value = kv[1].trim();
            }
            Config.setConfig(key, value);
        }
    }

    /**
     * 读取启动参数中的配置，如：-DMonitorAgent.service.name=tttt
     */
    private void readSystemArguments() {
        Properties properties = System.getProperties();
        properties.keySet().stream()
                .filter(key -> key.toString().startsWith(Constants.SYSTEM_PROPERTIES_PREFIX))
                .forEach(key -> Config.setConfig(key.toString().substring(Constants.SYSTEM_PROPERTIES_PREFIX.length()), properties.get(key).toString()));
    }

    /**
     * 读取本地文件配置
     *
     * @throws Exception
     */
    private void readLocalConfigFile() throws Exception {
        String configPath = System.getProperty(Constants.CONFIG_PATH);
        if (StringUtil.isEmpty(configPath)) {
            configPath = AgentPath.getCompatiblePath().getPath() + File.separator + "config" + File.separator + DEFAULT_CONFIG_FILE;
        }
		Config.addConfigProperties(configPath);
        log.info("Loaded config file path: {}", configPath);
    }

    private void readEnvConfigFile() {
        String serviceEnv = getActiveEnv();
        if (StringUtil.isEmpty(serviceEnv)) {
            return;
        }
		String envConfigPath = AgentPath.getCompatiblePath().getPath() + File.separator + "config" + File.separator + String.format(ENV_CONFIG_FILE_FORMAT, serviceEnv);
		File configFile = new File(envConfigPath);
        Properties envProperties = new Properties();
		if (configFile.exists()) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
                envProperties.load(reader);
            } catch (Exception e) {
                // ignore
            }
		}
		Config.addConfigProperties(envProperties);
	}

    private String getActiveEnv() {
        String serviceEnv = Config.getKv(Constants.SERVICE_ENV);
        if (StringUtil.isEmpty(serviceEnv)) {
            serviceEnv = Constants.DEFAULT_SERVICE_ENV;
        }
        log.info("The active env is {}", serviceEnv);
        return serviceEnv;
    }

	private void readAgentVersion() {
		String versionPath = AgentPath.getCompatiblePath().getPath() + File.separator + "version.txt";
		Path path = Paths.get(versionPath);
		if (!Files.exists(path)) {
			return;
		}

		String version = null;
		try (Stream<String> allLines = Files.lines(path)) {
			version = allLines.findFirst().get();
		} catch (IOException e) {}
		if (version != null) {
			Config.setConfig(Constants.AGENT_VERSION, version);
		}
		log.info("Loaded agent version is {}", version);
	}

    @Override
    public int weight() {
        return 99;
    }

}
