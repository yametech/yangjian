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

package com.yametech.yangjian.agent.tests.okhttp;

import com.yametech.yangjian.agent.tests.tool.AbstractHttpClientTest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import zipkin2.Span;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OkHttpPluginTest extends AbstractHttpClientTest {

    private static OkHttpClient okHttpClient;

    @BeforeClass
    public static void setUp() {
        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    @Test
    public void testGet() throws Exception {
        String path = "/";
        Request request = new Request.Builder().url(getBaseUrl() + path).build();
        Response response = okHttpClient.newCall(request).execute();
        //TimeUnit.SECONDS.sleep(5);
        List<Span> spanList = mockTracerServer.waitForSpans(1, Duration.ofSeconds(5).toMillis());
        assertEquals(1, spanList.size());
        Span span = spanList.get(0);
        assertEquals(getBaseUrl() + path, span.name());
        assertEquals("CLIENT", span.kind().name());
        Map<String, String> tags = span.tags();
        assertNotNull(tags);
        assertEquals("GET", tags.get("http.method"));
        assertEquals("200", tags.get("status_code"));
    }

    @Test
    public void testError() throws Exception {
        String path = "/error";
        Request request = new Request.Builder().url(getBaseUrl() + path).build();
        Response response = okHttpClient.newCall(request).execute();
        //TimeUnit.SECONDS.sleep(5);
        List<Span> spanList = mockTracerServer.waitForSpans(1, Duration.ofSeconds(5).toMillis());
        assertEquals(1, spanList.size());
        Span span = spanList.get(0);
        assertEquals(getBaseUrl() + path, span.name());
        assertEquals("CLIENT", span.kind().name());
        Map<String, String> tags = span.tags();
        assertNotNull(tags);
        assertEquals("GET", tags.get("http.method"));
        assertEquals("500", tags.get("status_code"));
    }

    @Test
    public void testRedirect() throws Exception {
        String path = "/redirect";
        Request request = new Request.Builder().url(getBaseUrl() + path).build();
        Response response = okHttpClient.newCall(request).execute();
        //TimeUnit.SECONDS.sleep(5);
        List<Span> spanList = mockTracerServer.waitForSpans(1, Duration.ofSeconds(5).toMillis());
        assertEquals(1, spanList.size());
        Span span = spanList.get(0);
        assertEquals(getBaseUrl() + path, span.name());
        assertEquals("CLIENT", span.kind().name());
        Map<String, String> tags = span.tags();
        assertNotNull(tags);
        assertEquals("GET", tags.get("http.method"));
        assertEquals("200", tags.get("status_code"));
    }
}
