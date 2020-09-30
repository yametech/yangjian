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
import zipkin2.Span;
import zipkin2.codec.SpanBytesDecoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author dengliming
 * @date 2020/9/7
 */
public class MockTracerServer {

    private final List<Span> spans = new ArrayList<>();
    private Undertow server;

    public MockTracerServer() {
        server = Undertow.builder()
                .addHttpListener(9411, "localhost",
                        new RoutingHandler().post("/api/v2/spans", new BlockingHandler(new SpanHttpHandler())))
                .build();
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop();
    }

    private List<Span> getSpans() {
        List<Span> copy = new ArrayList<>(spans.size());
        copy.addAll(spans);
        return copy;
    }

    public List<Span> waitForSpans(int number) {
        return waitForSpans(number, TimeUnit.SECONDS.toMillis(10));
    }

    public List<Span> waitForSpans(int number, long timeoutMillis) {
        long endMillis = System.currentTimeMillis() + timeoutMillis;
        List<Span> spans = getSpans();
        while (spans.size() < number && System.currentTimeMillis() < endMillis) {
            spans = getSpans();
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException e) {
            }
        }
        sortSpans(spans);
        return spans;
    }

    private void sortSpans(List<Span> spans) {
        if (spans == null || spans.size() <= 1) {
            return;
        }

        spans.sort(Comparator.comparing(Span::timestampAsLong));
    }

    public void clear() {
        spans.clear();
    }

    private void addSpans(List<Span> addSpans) {
        spans.addAll(addSpans);
    }

    class SpanHttpHandler implements HttpHandler {

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String body = builder.toString();
            if (body != null && !"".equals(body)) {
                try {
                    addSpans(SpanBytesDecoder.JSON_V2.decodeList(body.getBytes()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("received<<<<<" + body);
        }
    }
}
