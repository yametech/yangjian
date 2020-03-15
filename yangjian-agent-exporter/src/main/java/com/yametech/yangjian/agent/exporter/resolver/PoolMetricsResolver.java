/**
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
package com.yametech.yangjian.agent.exporter.resolver;

import com.yametech.yangjian.agent.exporter.metric.GaugeMetricFamily;
import com.yametech.yangjian.agent.exporter.model.MetricsParameter;
import io.prometheus.client.Collector;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 连接池指标
 *
 * @author dengliming
 * @date 2020/3/8
 */
public class PoolMetricsResolver implements IMetricsResolver<MetricsParameter, List<Collector.MetricFamilySamples>> {

    private static Pattern POOL_PATTERN = Pattern.compile("statistic/(.*)/connectionPool");
    private static final List<String> LABEL_NAMES = Arrays.asList("serviceName", "instance", "pool", "sign");

    @Override
    public boolean supports(MetricsParameter metricsParameter) {
        if (metricsParameter == null) {
            return false;
        }
        return MapUtils.getString(metricsParameter.getParams(), "dataType", "").contains("connectionPool");
    }

    /**
     * dataType=statistic/hikaricp/connectionPool&sign=jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai&active_count=0&serviceName=test&max_total=500&second=1584114173&ip=127.0.0.1
     *
     * @param metricsParameter
     * @return
     */
    @Override
    public List<Collector.MetricFamilySamples> resolve(MetricsParameter metricsParameter) {
        String serviceName = metricsParameter.getServiceName();
        String instance = metricsParameter.getInstance();
        Map<String, Object> params = metricsParameter.getParams();
        String sign = MapUtils.getString(params, "sign");
        List<Collector.MetricFamilySamples> sampleFamilies = new ArrayList<>();
        String poolType = extractPoolType(MapUtils.getString(metricsParameter.getParams(), "dataType", ""));
        List<String> labelValues = Arrays.asList(serviceName, instance, poolType, sign);
        sampleFamilies.add(new GaugeMetricFamily(
                "pool_monitor_active_count",
                "The number of active in the connectionPool",
                LABEL_NAMES, labelValues,
                MapUtils.getInteger(params, "active_count", 0), metricsParameter.getTimestamp()));

        sampleFamilies.add(new GaugeMetricFamily(
                "pool_monitor_max_total",
                "The number of max total in the connectionPool",
                LABEL_NAMES, labelValues,
                MapUtils.getInteger(params, "max_total", 0), metricsParameter.getTimestamp()));
        return sampleFamilies;
    }

    /**
     * 提取连接池类型（格式如：statistic/hikaricp/connectionPool）
     *
     * @return
     */
    private String extractPoolType(String dataType) {
        Matcher matcher = POOL_PATTERN.matcher(dataType);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Unknown";
    }
}
