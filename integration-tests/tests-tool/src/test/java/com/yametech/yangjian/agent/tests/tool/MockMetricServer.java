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

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author dengliming
 * @date 2020/9/7
 */
public class MockMetricServer {

    private Undertow server;

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
            System.out.println("received2<<<<<" + body);
        }
    }

    private void parseMetrics(String body) {
        // TODO
    }
}
