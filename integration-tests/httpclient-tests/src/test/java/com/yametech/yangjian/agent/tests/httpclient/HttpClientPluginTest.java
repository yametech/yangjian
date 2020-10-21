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

package com.yametech.yangjian.agent.tests.httpclient;

import com.yametech.yangjian.agent.tests.tool.AbstractHttpClientTest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import zipkin2.Span;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author dengliming
 * @date 2020/9/22
 */
public class HttpClientPluginTest extends AbstractHttpClientTest {

    private static CloseableHttpClient client;

    @BeforeClass
    public static void setUp() {
        client = HttpClients.createDefault();
    }

    @AfterClass
    public static void close() throws IOException {
        client.close();
    }

    @Test
    public void testGet() throws Exception {
        CloseableHttpResponse response = client.execute(new HttpGet(getBaseUrl() + "/"));
        response.getStatusLine().getStatusCode();
        response.close();
        List<Span> spans = mockTracerServer.waitForSpans(1, Duration.ofSeconds(5).toMillis());
        assertNotNull(spans);
        assertEquals(1, spans.size());
        Span span = spans.get(0);
        Map<String, String> tags = span.tags();
        assertNotNull(tags);
        assertEquals(getBaseUrl() + "/", tags.get("url"));
        assertEquals("GET", tags.get("http.method"));
        assertEquals("httpclient", tags.get("_component"));
        assertEquals("/", span.name());
    }

    @Test
    public void testError() throws Exception {
        String path = "/error";
        CloseableHttpResponse response = client.execute(new HttpGet(getBaseUrl() + path));
        response.getStatusLine().getStatusCode();
        response.close();
        List<Span> spans = mockTracerServer.waitForSpans(1, Duration.ofSeconds(5).toMillis());
        assertNotNull(spans);
        assertEquals(1, spans.size());
        Span span = spans.get(0);
        Map<String, String> tags = span.tags();
        assertNotNull(tags);
        assertEquals(getBaseUrl() + path, tags.get("url"));
        assertEquals("GET", tags.get("http.method"));
        assertEquals("500", tags.get("status_code"));
        assertEquals(path, span.name());
    }

    @Test
    public void testRedirect() throws Exception {
        String path = "/redirect";
        CloseableHttpResponse response = client.execute(new HttpGet(getBaseUrl() + path));
        response.getStatusLine().getStatusCode();
        List<Span> spans = mockTracerServer.waitForSpans(1, Duration.ofSeconds(5).toMillis());
        assertNotNull(spans);
        assertEquals(1, spans.size());
        Span span = spans.get(0);
        Map<String, String> tags = span.tags();
        assertNotNull(tags);
        assertEquals(getBaseUrl() + path, tags.get("url"));
        assertEquals("GET", tags.get("http.method"));
        assertEquals("200", tags.get("status_code"));
        assertEquals(path, span.name());
        response.close();
    }
}
