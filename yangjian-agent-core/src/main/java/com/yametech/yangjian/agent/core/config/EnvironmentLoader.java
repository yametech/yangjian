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
import com.yametech.yangjian.agent.util.OSUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 读取当前应用环境（兼容Apollo配置方式）
 *
 * @author dengliming
 * @date 2020/8/24
 */
public class EnvironmentLoader implements IConfigLoader {

    private static ILogger log = LoggerFactory.getLogger(EnvironmentLoader.class);
    private static final String SERVER_PROPERTIES_LINUX = "/opt/settings/server.properties";
    private static final String SERVER_PROPERTIES_WINDOWS = "C:/opt/settings/server.properties";

    @Override
    public void load(String arguments) throws Exception {
        String path = OSUtil.isWindows() ? SERVER_PROPERTIES_WINDOWS : SERVER_PROPERTIES_LINUX;
        File configFile = new File(path);
        Properties envProperties = new Properties();
        if (configFile.exists()) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
                envProperties.load(reader);
            } catch (Exception e) {
                // ignore
            }
        }

        String env = System.getProperty(Constants.SERVICE_ENV);
        if (StringUtil.notEmpty(env)) {
            log.info("Environment is set to [{}] by JVM system property 'service.env'.", env);
        } else {
            env = envProperties.getProperty("env");
            if (Constants.ENV_PRO.equalsIgnoreCase(env)) {
                env = Constants.ENV_PROD;
            }
            log.info("Environment is set to [{}] by property 'env' in server.properties.", env);
        }
        if (StringUtil.notEmpty(env)) {
            Config.setConfig(Constants.SERVICE_ENV, env);
        }
    }

    /**
     * 在LocalConfigLoader前面
     *
     * @return
     */
    @Override
    public int weight() {
        return 100;
    }
}
