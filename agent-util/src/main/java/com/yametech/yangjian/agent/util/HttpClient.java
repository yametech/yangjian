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
package com.yametech.yangjian.agent.util;

import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author dengliming
 * @date 2020/3/5
 */
public class HttpClient {

    private static final ILogger LOGGER = LoggerFactory.getLogger(HttpClient.class);
    private static final int DEFAULT_TIME_OUT = 3000;

    public static HttpResponse doHttpRequest(HttpRequest request) {
        String requestUrl = request.getRequestUrl();
        if (StringUtil.isEmpty(requestUrl)) {
            return null;
        }

        HttpResponse response = new HttpResponse();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(requestUrl).openConnection();
            conn.setReadTimeout(DEFAULT_TIME_OUT);
            conn.setConnectTimeout(DEFAULT_TIME_OUT);
            conn.setRequestMethod(request.getMethod().name());
            if (request.getHeaders() != null) {
                setHttpHeader(conn, request.getHeaders());
            }

            if (request.getDatas() != null) {
                conn.setDoOutput(true);
                writeToStream(conn.getOutputStream(), request.getDatas());
            }

            response.setCode(conn.getResponseCode());
            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                response.setData(readResponseAsString(conn.getInputStream()));
            }
        } catch (Exception e) {
            LOGGER.error(e, "doHttpRequest(url:{}) error.", requestUrl);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response;
    }

    private static void setHttpHeader(HttpURLConnection conn, Map<String, String> headers) {
        headers.forEach((k, v) -> conn.setRequestProperty(k, v));
    }

    private static String readResponseAsString(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            LOGGER.error(e, "readResponseAsString error.");
        }
        return sb.toString();
    }

    private static void writeToStream(OutputStream os, String datas) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, StandardCharsets.UTF_8.name()))) {
            writer.write(datas);
        } catch (Exception e) {
            LOGGER.error(e, "writeStream error.");
        }
    }
}
