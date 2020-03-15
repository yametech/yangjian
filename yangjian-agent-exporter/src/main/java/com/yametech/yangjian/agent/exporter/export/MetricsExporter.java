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
package com.yametech.yangjian.agent.exporter.export;

import com.yametech.yangjian.agent.exporter.model.MetricsParameter;
import com.yametech.yangjian.agent.exporter.resolver.*;
import com.yametech.yangjian.agent.exporter.storage.DiskMetricStore;
import io.prometheus.client.Collector;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dengliming
 * @date 2020/3/8
 */
public class MetricsExporter extends Collector {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsExporter.class);
    private List<IMetricsResolver> metricsResolvers = new ArrayList<>();
    private DiskMetricStore diskMetricStore;
    private static final String PARAM_SECOND_KEY = "second";
    private static final String PARAM_IP_KEY = "ip";
    private static final String PARAM_SERVICE_NAME_KEY = "serviceName";

    public MetricsExporter(DiskMetricStore diskMetricStore) {
        metricsResolvers.add(new ThreadMetricsResolver());
        metricsResolvers.add(new JVMGcMetricsResolver());
        metricsResolvers.add(new ClassMetricsResolver());
        metricsResolvers.add(new PoolMetricsResolver());
        metricsResolvers.add(new StatisticsMetricsResolver());
        metricsResolvers.add(new ProcessMetricsResolver());
        this.diskMetricStore = diskMetricStore;
    }

    @Override
    public List<MetricFamilySamples> collect() {
        try {
            List<String> metrics = diskMetricStore.getMetrics();
            if (metrics == null) {
                return Collections.EMPTY_LIST;
            }
            List<MetricFamilySamples> allSampleFamilies = new ArrayList<>();
            for (String metric : metrics) {
                MetricsParameter metricsParameter = buildMetricsParameter(metric);
                // params required not empty
                if (StringUtils.isBlank(metricsParameter.getDataType())
                        || StringUtils.isBlank(metricsParameter.getInstance())
                        || StringUtils.isBlank(metricsParameter.getServiceName())) {
                    continue;
                }

                for (IMetricsResolver metricsResolver : metricsResolvers) {
                    if (!metricsResolver.supports(metricsParameter)) {
                        continue;
                    }
                    List<MetricFamilySamples> sampleFamilies = (List<MetricFamilySamples>) metricsResolver.resolve(metricsParameter);
                    if (!CollectionUtils.isEmpty(sampleFamilies)) {
                        allSampleFamilies.addAll(sampleFamilies);
                    }
                }
            }
            return allSampleFamilies;
        } catch (Exception e) {
            LOGGER.error("collect error.", e);
        }

        return Collections.EMPTY_LIST;
    }

    private MetricsParameter buildMetricsParameter(String metric) {
        MetricsParameter metricsParameter = new MetricsParameter();
        Map<String, Object> params = Arrays.stream(metric.split("&"))
                .map(s -> s.split("="))
                .filter(s -> {
                    if (s.length != 2) {
                        return false;
                    }

                    if (PARAM_SECOND_KEY.equals(s[0])) {
                        long second = NumberUtils.toLong(s[1], 0);
                        metricsParameter.setTimestamp(second == 0 ? System.currentTimeMillis() : second * 1000L);
                        return false;
                    }

                    if (PARAM_IP_KEY.equals(s[0])) {
                        metricsParameter.setInstance(s[1]);
                        return false;
                    }

                    if (PARAM_SERVICE_NAME_KEY.equals(s[0])) {
                        metricsParameter.setServiceName(s[1]);
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toMap(s -> decode(s[0]), s -> decode(s[1])));
        metricsParameter.setParams(params);
        return metricsParameter;
    }

    private String decode(String str) {
        try {
            return URLDecoder.decode(str, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("decode(str:{}) error.", str, e);
        }
        return str;
    }
}
