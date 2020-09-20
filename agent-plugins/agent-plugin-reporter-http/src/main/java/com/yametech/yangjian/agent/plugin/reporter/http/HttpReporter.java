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
package com.yametech.yangjian.agent.plugin.reporter.http;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.IReport;
import com.yametech.yangjian.agent.api.bean.ConfigNotifyType;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.util.HttpClient;
import com.yametech.yangjian.agent.util.HttpRequest;
import com.yametech.yangjian.agent.util.HttpResponse;

/**
 * HTTP上报
 * <p>
 * 注：agent.properties配置report=http-span、report.http-span.url=xxx
 *
 * @author dengliming
 * @date 2020/3/5
 */
public class HttpReporter implements IReport, IConfigReader {
    private static final String URL_CONFIG_KEY = "report.http-span.url";
    private static final String SPAN_REPORT_TYPE = "http-span";
    /**
     * 上报的URL
     */
    private String url;

    @Override
    public boolean report(Object data) {
        if (StringUtil.isEmpty(url) || data == null) {
            return false;
        }

        HttpResponse httpResponse = HttpClient.doHttpRequest(new HttpRequest(url, HttpRequest.HttpMethod.POST)
                .setDatas(data.toString()));
        // 返回2XX即认为成功
        return httpResponse != null && httpResponse.is2xxSuccessful();
    }

    @Override
    public boolean batchReport(List<Object> datas) {
        for (Object obj : datas) {
            if (!report(obj)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String type() {
        return SPAN_REPORT_TYPE;
    }

    @Override
    public Set<String> configKey() {
        return new HashSet<>(Arrays.asList(URL_CONFIG_KEY.replaceAll("\\.", "\\\\.")));
    }

    @Override
    public void configKeyValue(Map<String, String> kv) {
        if (kv.containsKey(URL_CONFIG_KEY)) {
            url = kv.get(URL_CONFIG_KEY);
        }
    }

    @Override
    public ConfigNotifyType notifyType() {
        return ConfigNotifyType.ALWAYS;
    }

}