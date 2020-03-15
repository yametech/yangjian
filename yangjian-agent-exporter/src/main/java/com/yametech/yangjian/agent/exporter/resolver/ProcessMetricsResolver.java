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

/**
 * 进程cpu、内存相关指标
 *
 * @author dengliming
 * @date 2020/3/14
 */
public class ProcessMetricsResolver implements IMetricsResolver<MetricsParameter, List<Collector.MetricFamilySamples>> {

    private static final List<String> LABEL_NAMES = Arrays.asList("serviceName", "instance");

    @Override
    public boolean supports(MetricsParameter metricsParameter) {
        if (metricsParameter == null || metricsParameter.getParams() == null) {
            return false;
        }
        return metricsParameter.getParams().containsKey("cpu");
    }

    /**
     * oldgen=21506&eden=298901&dataType=resources&cpu=0.0&code_cache=16140&non_heap=59420&permgen=5077&serviceName=test&survivor=4763&second=1584114175&mapped_cache=0&direct_memory=16&metaspace=38202&heap=325171&memory_total=0.0&ip=127.0.0.1
     *
     * @param metricsParameter
     * @return
     */
    @Override
    public List<Collector.MetricFamilySamples> resolve(MetricsParameter metricsParameter) {
        long timestamp = metricsParameter.getTimestamp();
        String serviceName = metricsParameter.getServiceName();
        String instance = metricsParameter.getInstance();
        Map<String, Object> params = metricsParameter.getParams();
        List<Collector.MetricFamilySamples> sampleFamilies = new ArrayList<>();
        List<String> labelValues = Arrays.asList(serviceName, instance);
        sampleFamilies.add(new GaugeMetricFamily(
                "process_cpu_usage_percent",
                "Cpu usage percent",
                LABEL_NAMES, labelValues,
                MapUtils.getDoubleValue(params, "cpu", 0.0), timestamp));

        sampleFamilies.add(new GaugeMetricFamily(
                "process_memory_total",
                "Memory usage total",
                LABEL_NAMES, labelValues,
                MapUtils.getInteger(params, "memory_total", 0), timestamp));

        sampleFamilies.add(new GaugeMetricFamily(
                "process_memory_non_heap",
                "The non_heap memory in the process",
                LABEL_NAMES, labelValues,
                MapUtils.getInteger(params, "non_heap", 0), timestamp));

        sampleFamilies.add(new GaugeMetricFamily(
                "process_memory_heap",
                "The heap memory in the process",
                LABEL_NAMES, labelValues,
                MapUtils.getInteger(params, "heap", 0), timestamp));

        sampleFamilies.add(new GaugeMetricFamily(
                "process_memory_mapped_cache",
                "The mapped_cache memory in the process",
                LABEL_NAMES, labelValues,
                MapUtils.getInteger(params, "mapped_cache", 0), timestamp));

        sampleFamilies.add(new GaugeMetricFamily(
                "process_memory_direct",
                "The direct memory in the process",
                LABEL_NAMES, labelValues,
                MapUtils.getInteger(params, "direct_memory", 0), timestamp));

        sampleFamilies.add(new GaugeMetricFamily(
                "process_memory_oldgen",
                "The oldgen memory in the process",
                LABEL_NAMES, labelValues,
                MapUtils.getInteger(params, "oldgen", 0), timestamp));

        sampleFamilies.add(new GaugeMetricFamily(
                "process_memory_eden",
                "The eden memory in the process",
                LABEL_NAMES, labelValues,
                MapUtils.getInteger(params, "eden", 0), timestamp));

        sampleFamilies.add(new GaugeMetricFamily(
                "process_memory_permgen",
                "The permgen memory in the process",
                LABEL_NAMES, labelValues,
                MapUtils.getInteger(params, "permgen", 0), timestamp));

        sampleFamilies.add(new GaugeMetricFamily(
                "process_memory_survivor",
                "The survivor memory in the process",
                LABEL_NAMES, labelValues,
                MapUtils.getInteger(params, "survivor", 0), timestamp));
        return sampleFamilies;
    }
}
