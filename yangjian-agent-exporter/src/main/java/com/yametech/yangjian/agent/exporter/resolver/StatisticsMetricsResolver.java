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
 * QPS/RT指标统计解析
 *
 * @author dengliming
 * @date 2020/3/8
 */
public class StatisticsMetricsResolver implements IMetricsResolver<MetricsParameter, List<Collector.MetricFamilySamples>> {

    private static Pattern TYPE_PATTERN = Pattern.compile("statistic/(.*)/RT");
    private static final List<String> LABEL_NAME = Arrays.asList("serviceName", "instance", "type", "sign");

    @Override
    public boolean supports(MetricsParameter metricsParameter) {
        if (metricsParameter == null) {
            return false;
        }
        return MapUtils.getString(metricsParameter.getParams(), "dataType", "").contains("/RT");
    }

    /**
     * rt_max=0&rt_min=0&num=1&dataType=statistic/http-server/RT&sign=com.yametech.yangjian.agent.benchmark.Application.ping()&rt_total=0&serviceName=test&second=1584114171&ip=127.0.0.1
     *
     * @param metricsParameter
     * @return
     */
    @Override
    public List<Collector.MetricFamilySamples> resolve(MetricsParameter metricsParameter) {
        String serviceName = metricsParameter.getServiceName();
        String instance = metricsParameter.getInstance();
        Map<String, Object> params = metricsParameter.getParams();
        String type = extractType(MapUtils.getString(params, "dataType"));
        String sign = MapUtils.getString(params, "sign");
        int num = MapUtils.getIntValue(params, "num", 0);
        int rtTotal = MapUtils.getIntValue(params, "rt_total", 0);
        List<Collector.MetricFamilySamples> sampleFamilies = new ArrayList<>();
        GaugeMetricFamily qpsMetricFamily = new GaugeMetricFamily("request_per_second", "QPS", LABEL_NAME);
        qpsMetricFamily.addMetric(Arrays.asList(serviceName, instance, type, sign), num, metricsParameter.getTimestamp());
        sampleFamilies.add(qpsMetricFamily);

        GaugeMetricFamily rtMetricFamily = new GaugeMetricFamily("response_time", "RT", LABEL_NAME);
        rtMetricFamily.addMetric(Arrays.asList(serviceName, instance, type, sign), num > 0 ? rtTotal / num * 1.0 : 0, metricsParameter.getTimestamp());
        sampleFamilies.add(rtMetricFamily);
        return sampleFamilies;
    }

    private String extractType(String dataType) {
        Matcher matcher = TYPE_PATTERN.matcher(dataType);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Unknown";
    }
}
