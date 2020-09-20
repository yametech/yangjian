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
package com.yametech.yangjian.agent.server.metric;

import io.prometheus.client.Collector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CounterMetricFamily extends Collector.MetricFamilySamples {

    private final List<String> labelNames;

    public CounterMetricFamily(String name, String help, double value) {
        this(name, help, value, null);
    }

    public CounterMetricFamily(String name, String help, double value, Long timestamp) {
        super(name, Collector.Type.COUNTER, help, new ArrayList<Sample>());
        labelNames = Collections.emptyList();
        samples.add(
                new Sample(
                        name,
                        labelNames,
                        Collections.<String>emptyList(),
                        value, timestamp));
    }

    public CounterMetricFamily(String name, String help, List<String> labelNames, List<String> labelValues, double value, Long timestamp) {
        super(name, Collector.Type.COUNTER, help, new ArrayList<Sample>());
        this.labelNames = labelValues;
        samples.add(new Sample(name, labelNames, labelValues, value, timestamp));
    }

    public CounterMetricFamily(String name, String help, List<String> labelNames) {
        super(name, Collector.Type.COUNTER, help, new ArrayList<Sample>());
        this.labelNames = labelNames;
    }

    public CounterMetricFamily addMetric(List<String> labelValues, double value) {
        return addMetric(labelValues, value, null);
    }

    public CounterMetricFamily addMetric(List<String> labelValues, double value, Long timestamp) {
        if (labelValues.size() != labelNames.size()) {
            throw new IllegalArgumentException("Incorrect number of labels.");
        }
        samples.add(new Sample(name, labelNames, labelValues, value));
        return this;
    }
}