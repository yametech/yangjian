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
package com.yametech.yangjian.agent.server.resolver;

import com.yametech.yangjian.agent.server.metric.GaugeMetricFamily;
import com.yametech.yangjian.agent.server.model.MetricsParameter;
import io.prometheus.client.Collector;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 线程相关指标解析
 *
 * @author dengliming
 * @date 2020/3/8
 */
public class ThreadMetricsResolver implements IMetricsResolver<MetricsParameter, List<Collector.MetricFamilySamples>> {

    @Override
    public boolean supports(MetricsParameter metricsParameter) {
        if (metricsParameter == null) {
            return false;
        }
        return "resources".equals(MapUtils.getString(metricsParameter.getParams(), "dataType"))
                && metricsParameter.getParams().containsKey("thread_total_started");
    }

    @Override
    public List<Collector.MetricFamilySamples> resolve(MetricsParameter metricsParameter) {
        String instance = metricsParameter.getInstance();
        String serviceName = metricsParameter.getServiceName();
        Map<String, Object> params = metricsParameter.getParams();
        List<Collector.MetricFamilySamples> sampleFamilies = new ArrayList<>();
        GaugeMetricFamily daemonMetricFamily = new GaugeMetricFamily(
                "jvm_threads_daemon",
                "Daemon thread count of a JVM",
                Arrays.asList("serviceName", "instance"));
        daemonMetricFamily.addMetric(Arrays.asList(serviceName, instance), MapUtils.getIntValue(params, "thread_daemon", 0), metricsParameter.getTimestamp());
        sampleFamilies.add(daemonMetricFamily);

        GaugeMetricFamily peakMetricFamily = new GaugeMetricFamily(
                "jvm_threads_peak",
                "Peak thread count of a JVM",
                Arrays.asList("serviceName", "instance"));
        peakMetricFamily.addMetric(Arrays.asList(serviceName, instance), MapUtils.getIntValue(params, "thread_peak", 0), metricsParameter.getTimestamp());
        sampleFamilies.add(peakMetricFamily);

        GaugeMetricFamily startedTotalMetricFamily = new GaugeMetricFamily(
                "jvm_threads_started_total",
                "Started thread count of a JVM",
                Arrays.asList("serviceName", "instance"));
        startedTotalMetricFamily.addMetric(Arrays.asList(serviceName, instance), MapUtils.getIntValue(params, "thread_total_started", 0), metricsParameter.getTimestamp());
        sampleFamilies.add(startedTotalMetricFamily);

        GaugeMetricFamily threadStateFamily = new GaugeMetricFamily(
                "jvm_threads_state",
                "Current count of threads by state",
                Arrays.asList("state", "serviceName", "instance"));
        threadStateFamily.addMetric(Arrays.asList(Thread.State.NEW.name(), serviceName, instance), MapUtils.getIntValue(params, "thread_news", 0), metricsParameter.getTimestamp());
        threadStateFamily.addMetric(Arrays.asList(Thread.State.RUNNABLE.name(), serviceName, instance), MapUtils.getIntValue(params, "thread_runnable", 0), metricsParameter.getTimestamp());
        threadStateFamily.addMetric(Arrays.asList(Thread.State.BLOCKED.name(), serviceName, instance), MapUtils.getIntValue(params, "thread_blocked", 0), metricsParameter.getTimestamp());
        threadStateFamily.addMetric(Arrays.asList(Thread.State.WAITING.name(), serviceName, instance), MapUtils.getIntValue(params, "thread_waiting", 0), metricsParameter.getTimestamp());
        threadStateFamily.addMetric(Arrays.asList(Thread.State.TIMED_WAITING.name(), serviceName, instance), MapUtils.getIntValue(params, "thread_timed_waiting", 0), metricsParameter.getTimestamp());
        threadStateFamily.addMetric(Arrays.asList(Thread.State.TERMINATED.name(), serviceName, instance), MapUtils.getIntValue(params, "thread_terminated", 0), metricsParameter.getTimestamp());
        sampleFamilies.add(threadStateFamily);
        return sampleFamilies;
    }
}
