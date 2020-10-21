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

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.IReport;
import com.yametech.yangjian.agent.api.bean.ConfigNotifyType;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.util.HttpClient;
import com.yametech.yangjian.agent.util.HttpRequest;
import com.yametech.yangjian.agent.util.HttpResponse;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dengliming
 */
public abstract class AbstractHttpReporter implements IReport, IConfigReader {

    /**
     * 上报的URL
     */
    protected String url;

    @Override
    public Set<String> configKey() {
        String urlConfigKey = getConfigKey();
        if (urlConfigKey == null) {
            return null;
        }
        return new HashSet<>(Arrays.asList(urlConfigKey.replaceAll("\\.", "\\\\.")));
    }

    @Override
    public void configKeyValue(Map<String, String> kv) {
        String urlConfigKey = getConfigKey();
        if (urlConfigKey == null) {
            return;
        }
        if (kv.containsKey(urlConfigKey)) {
            url = kv.get(urlConfigKey);
        }
    }

    public abstract String getConfigKey();

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
    public ConfigNotifyType notifyType() {
        return ConfigNotifyType.ALWAYS;
    }
}
