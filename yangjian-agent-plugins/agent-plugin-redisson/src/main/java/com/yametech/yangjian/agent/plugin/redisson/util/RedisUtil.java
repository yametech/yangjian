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
package com.yametech.yangjian.agent.plugin.redisson.util;

import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.bean.MetricData;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MultiReportFactory;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dengliming
 * @date 2020/7/27
 */
public class RedisUtil {

    private static IReportData report = MultiReportFactory.getReport("collect");

    public static String buildRedisUrl(Collection nodeAddresses) {
        StringBuilder sb = new StringBuilder();
        if (nodeAddresses != null && !nodeAddresses.isEmpty()) {
            for (Object uri : nodeAddresses) {
                sb.append(getRealUrl(uri)).append(",");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String getRealUrl(Object obj) {
        if (obj instanceof String) {
            return ((String) obj).replace("redis://", "");
        } else if (obj instanceof URI) {
            URI uri = (URI) obj;
            return uri.getHost() + ":" + uri.getPort();
        }
        return null;
    }

    public static void reportDependency(String url, String dbMode) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.Tags.PEER, url);
        params.put(Constants.Tags.DB_MODE, dbMode);
        report.report(MetricData.get(null, Constants.DEPENDENCY_PATH + Constants.Component.REDISSON, params));
    }
}
