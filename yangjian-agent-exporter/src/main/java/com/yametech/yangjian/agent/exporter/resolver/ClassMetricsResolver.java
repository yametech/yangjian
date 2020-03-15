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

import com.yametech.yangjian.agent.exporter.metric.CounterMetricFamily;
import com.yametech.yangjian.agent.exporter.metric.GaugeMetricFamily;
import com.yametech.yangjian.agent.exporter.model.MetricsParameter;
import io.prometheus.client.Collector;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 类加载相关指标
 *
 * @author dengliming
 * @date 2020/3/8
 */
public class ClassMetricsResolver implements IMetricsResolver<MetricsParameter, List<Collector.MetricFamilySamples>> {

    private static final List<String> LABEL_NAMES = Arrays.asList("serviceName", "instance");
    @Override
    public boolean supports(MetricsParameter metricsParameter) {
        if (metricsParameter == null) {
            return false;
        }
        return metricsParameter.getParams().containsKey("class_loaded");
    }

    /**
     * class_loaded=7365&young_gc_count=0&class_unloaded=0&dataType=resources&full_gc_time=0&full_gc_count=0&class_total=7365&serviceName=test&young_gc_time=0&avg_young_gc_time=0.0&second=1584114174&ip=127.0.0.1
     *
     * @param metricsParameter
     * @return
     */
    @Override
    public List<Collector.MetricFamilySamples> resolve(MetricsParameter metricsParameter) {
        long timestamp = metricsParameter.getTimestamp();
        Map<String, Object> params = metricsParameter.getParams();
        List<Collector.MetricFamilySamples> sampleFamilies = new ArrayList<>();
        List<String> labelValues = Arrays.asList(metricsParameter.getServiceName(), metricsParameter.getInstance());
        sampleFamilies.add(new GaugeMetricFamily(
                "jvm_classes_loaded",
                "The number of classes that are currently loaded in the JVM",
                LABEL_NAMES, labelValues,
                MapUtils.getInteger(params, "class_loaded", 0), timestamp));
        sampleFamilies.add(new CounterMetricFamily(
                "jvm_classes_loaded_total",
                "The total number of classes that have been loaded since the JVM has started execution",
                LABEL_NAMES, labelValues,
                MapUtils.getInteger(params, "class_total", 0), timestamp));
        sampleFamilies.add(new CounterMetricFamily(
                "jvm_classes_unloaded_total",
                "The total number of classes that have been unloaded since the JVM has started execution",
                LABEL_NAMES, labelValues,
                MapUtils.getInteger(params, "class_unloaded", 0), timestamp));
        return sampleFamilies;
    }
}
