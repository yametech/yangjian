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

package com.yametech.yangjian.agent.tests.tool;

import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.tests.tool.bean.EventMetric;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author dengliming
 * @date 2020/9/7
 */
public class MockMetricServer {

    private Undertow server;
    private final List<EventMetric> metrics = new ArrayList<>();

    public MockMetricServer() {
        server = Undertow.builder()
                .addHttpListener(9412, "localhost",
                        new RoutingHandler().post("/api/metric/report", new BlockingHandler(new MetricHttpHandler())))
                .build();
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop();
    }

    private List<EventMetric> getMetrics() {
        List<EventMetric> copy = new ArrayList<>(metrics.size());
        copy.addAll(metrics);
        return copy;
    }

    public List<EventMetric> waitForMetrics(int number) {
        return waitForMetrics(number, TimeUnit.SECONDS.toMillis(10));
    }

    public List<EventMetric> waitForMetrics(int number, long timeoutMillis) {
        long endMillis = System.currentTimeMillis() + timeoutMillis;
        List<EventMetric> metrics = getMetrics();
        while (metrics.size() < number && System.currentTimeMillis() < endMillis) {
            metrics = getMetrics();
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException e) {
            }
        }
        sortMetrics(metrics);
        return metrics;
    }

    private void sortMetrics(List<EventMetric> metrics) {
        if (metrics == null || metrics.size() <= 1) {
            return;
        }

        metrics.sort(Comparator.comparing(EventMetric::getEventTime));
    }

    public void clear() {
        metrics.clear();
    }

    class MetricHttpHandler implements HttpHandler {

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();

            try {
                reader = new BufferedReader(new InputStreamReader(exchange.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            String body = builder.toString();
            if (body != null && !"".equals(body)) {
                parseMetrics(body);
            }
            System.out.println("received-metrics<<<<<" + body);
        }
    }

    // 格式：test/1602419501/statistic/http-client/RT?rt_max=965&rt_min=965&num=1&sign=http%3A%2F%2Flocalhost%3A49771%2F&error_total=0&rt_total=965
    private void parseMetrics(String body) {
        int start = body.indexOf("/RT");
        if (start == -1) {
            return;
        }

        String path = body.substring(0, start);
        if (StringUtil.isEmpty(path)) {
            return;
        }

        String[] paths = path.split("/");
        if (paths.length < 4) {
            return;
        }

        EventMetric eventMetric = new EventMetric();
        eventMetric.setServiceName(paths[0]);
        eventMetric.setEventTime(Long.parseLong(paths[1]));
        eventMetric.setType(paths[3]);
        String[] params = body.substring(start + 4).split("&");
        for (String param : params) {
            String[] kvs = param.split("=");
            String key = decode(kvs[0]);
            String val = decode(kvs[1]);
            if ("sign".equals(key)) {
                eventMetric.setSign(val);
            }
            if ("error_total".equals(key)) {
                eventMetric.setErrorTotal(Long.parseLong(val));
            }
            if ("rt_total".equals(key)) {
                eventMetric.setRtTotal(Long.parseLong(val));
            }
            if ("num".equals(key)) {
                eventMetric.setNum(Long.parseLong(val));
            }
        }
        metrics.add(eventMetric);
    }

    private static String decode(String str) {
        try {
            return URLDecoder.decode(str, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
