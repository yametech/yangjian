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
 * JVM垃圾回相关指标
 *
 * @author dengliming
 */
public class JVMGcMetricsResolver implements IMetricsResolver<MetricsParameter, List<Collector.MetricFamilySamples>> {

    private static final List<String> LABEL_NAMES = Arrays.asList("serviceName", "instance", "type");

    @Override
    public boolean supports(MetricsParameter metricsParameter) {
        if (metricsParameter == null) {
            return false;
        }
        return metricsParameter.getParams().containsKey("full_gc_count");
    }

    /**
     * class_loaded=7365&young_gc_count=0&class_unloaded=0&dataType=resources&full_gc_time=0&full_gc_count=0&class_total=7365&serviceName=test&young_gc_time=0&avg_young_gc_time=0.0&second=1584114174&ip=127.0.0.1
     *
     * @param metricsParameter
     * @return
     */
    @Override
    public List<Collector.MetricFamilySamples> resolve(MetricsParameter metricsParameter) {
        long t = metricsParameter.getTimestamp();
        String instance = metricsParameter.getInstance();
        String serviceName = metricsParameter.getServiceName();
        Map<String, Object> params = metricsParameter.getParams();
        List<Collector.MetricFamilySamples> sampleFamilies = new ArrayList<>();
        GaugeMetricFamily gcCountFamily = new GaugeMetricFamily("jvm_gc_count", "jvm_gc_count", LABEL_NAMES);
        gcCountFamily.addMetric(Arrays.asList(serviceName, instance, "full_gc_count"), MapUtils.getIntValue(params, "full_gc_count", 0), t);
        gcCountFamily.addMetric(Arrays.asList(serviceName, instance, "young_gc_count"), MapUtils.getIntValue(params, "young_gc_count", 0), t);
        sampleFamilies.add(gcCountFamily);

        GaugeMetricFamily gcTimeFamily = new GaugeMetricFamily("jvm_gc_time", "jvm_gc_time", LABEL_NAMES);
        gcTimeFamily.addMetric(Arrays.asList(serviceName, instance, "full_gc_time"), MapUtils.getIntValue(params, "full_gc_time", 0), t);
        gcTimeFamily.addMetric(Arrays.asList(serviceName, instance, "young_gc_time"), MapUtils.getIntValue(params, "young_gc_time", 0), t);
        sampleFamilies.add(gcTimeFamily);
        return sampleFamilies;
    }
}
