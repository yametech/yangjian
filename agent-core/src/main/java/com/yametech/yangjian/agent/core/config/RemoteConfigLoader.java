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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.yametech.yangjian.agent.api.IConfigLoader;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.common.Config;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.InstanceManage;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.util.HttpClient;
import com.yametech.yangjian.agent.util.HttpRequest;
import com.yametech.yangjian.agent.util.HttpResponse;
import com.yametech.yangjian.agent.util.json.JSONUtils;

/**
 * 通过http加载远程配置
 *
 * @author liuzhao
 * @author dengliming
 * @Description
 * @date 2019年10月6日 下午10:23:16
 */
public class RemoteConfigLoader implements IConfigLoader, ISchedule {

    private static final ILogger logger = LoggerFactory.getLogger(RemoteConfigLoader.class);
    /**
     * 请求远程配置最大重试次数
     */
    private static final int MAX_RETRY = 2;
    private static final String LOCAL_CONFIG_PATH = "/data/www/soft/mointor/config-cache";
    private static final String LOCAL_CONFIG_FILE_NAME = "agent-remote.properties";
    /**
     * monitor响应请求成功code
     */
    private static final int HTTP_RES_CODE_200 = 200;
    private static final int INTERVAL_SECONDS = 3 * 60;

    /**
     * 定时请求远程刷新秒数
     */
    @Override
    public int interval() {
        return INTERVAL_SECONDS;
    }

    /**
     * 首次执行延迟时间
     *
     * @return
     */
    @Override
    public int initialDelay() {
        return INTERVAL_SECONDS;
    }

    /**
     * 定时每隔3分钟向远程拉取配置
     */
    @Override
    public void execute() {
        logger.debug("[Ecpark-Agent] load remote config start...");
        refreshConfig(false);
        logger.debug("[Ecpark-Agent] load remote config end...");
    }

    /**
     * agent初始化时会执行一次
     *
     * @param arguments
     */
    @Override
    public void load(String arguments) {
        refreshConfig(true);
    }

    @Override
    public int weight() {
        return 9;
    }

    /**
     * 远程同步配置
     *
     * @param first 是否第一次加载
     */
    private void refreshConfig(boolean first) {
        String url = Config.getKv(Constants.CONFIG_REMOTE_URL);
        String env = Config.getKv(Constants.SERVICE_ENV, "");
        String serviceName = Config.SERVICE_NAME.getValue();
        // 要配置远程url和应用名才去获取远程配置
        if (StringUtil.isEmpty(url) || StringUtil.isEmpty(serviceName)) {
            return;
        }
        url = url + "?appName=" + serviceName + "&env=" + env;
        try {
            Map<String, String> configMap = getRemoteServerConfig(url, MAX_RETRY);
            // 规定接口没有配置也要返回空值覆盖
            if (configMap != null) {
                Properties properties = new Properties();
                properties.putAll(configMap);
                if(!first) {
                	InstanceManage.refreshConfig(new HashMap<>(configMap));
                }
                Config.refreshRemoteConfig(configMap);
                storeLocalCacheFile(properties);
                return;
            }

            // 远程加载失败并且是初始化时的第一次加载则降级读取本地缓存配置
            if (first) {
                Properties properties = loadLocalCacheFile();
                if (properties != null) {
                    Config.refreshRemoteConfig(properties.entrySet()
                            .stream()
                            .collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue()))));
                }
            }
        } catch (IOException e) {
            logger.error(e, "[Ecpark-Agent] load remote config {} error.", url, e);
        }
    }

    /**
     * 获取远程配置
     *
     * @param url
     * @param retryTimes
     * @return
     */
    private Map<String, String> getRemoteServerConfig(String url, int retryTimes) {
        int sleepSeconds = 2;
        do {
            HttpResponse<String> response = null;
            int code = -1;
            Map<String, Object> responseMap = null;
            // 允许重试多次
            try {
                response = HttpClient.doHttpRequest(new HttpRequest(url, HttpRequest.HttpMethod.GET));
                if (response != null && StringUtil.notEmpty(response.getData())) {
                    responseMap = (Map<String, Object>) JSONUtils.parse(response.getData());
                    code = Integer.parseInt(responseMap.getOrDefault("code", -99).toString());
                    if (code == HTTP_RES_CODE_200) {
                        //{"code":200,"msg":"success","data":[{"configKey":"test1","configValue":"2"}]}
                        List<Map<String, String>> configList = (List<Map<String, String>>) responseMap.get("data");
                        if (configList != null) {
                            return configList.stream()
                                    .collect(Collectors.toMap(k -> k.get("configKey"), v -> v.get("configValue")));
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e, "[Ecpark-Agent] getRemoteServerConfig(url:{}, response:{}) error.", url, response);
            }
            retryTimes--;
            logger.warn(
                    "[Ecpark-Agent] getRemoteServerConfig(url:{}, response:{}) failed, will retry in {} seconds.", url, response, sleepSeconds);
            try {
                TimeUnit.SECONDS.sleep(sleepSeconds);
            } catch (InterruptedException ex) {
                // ignore
            }
        } while (retryTimes > 0);
        return null;
    }

    /**
     * 从本地磁盘加载远程配置
     *
     * @return
     * @throws IOException
     */
    private Properties loadLocalCacheFile() throws IOException {
        File configFile = new File(getLocalConfigPath());
        Properties properties = null;
        if (configFile.exists()) {
            properties = new Properties();
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
        }
        return properties;
    }

    /**
     * 保存一份到本地磁盘（防止远程配置服务不可用）
     *
     * @param properties
     * @throws IOException
     */
    private void storeLocalCacheFile(Properties properties) throws IOException {
        File file = new File(getLocalConfigPath());
        try (OutputStream out = new FileOutputStream(file)) {
            properties.store(out, "Stored by Ecpark-Agent");
        }
    }

    /**
     * 获取本地磁盘缓存的路径
     *
     * @return /data/www/config-cache/xxxx/agent-remote.properties
     * @throws IOException
     */
    private String getLocalConfigPath() throws IOException {
        String serviceName = Config.SERVICE_NAME.getValue();
        String dir = LOCAL_CONFIG_PATH + File.separator + serviceName;
        // 判断文件夹不存在则创建
        Path path = Paths.get(dir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return dir + File.separator + LOCAL_CONFIG_FILE_NAME;
    }
}
