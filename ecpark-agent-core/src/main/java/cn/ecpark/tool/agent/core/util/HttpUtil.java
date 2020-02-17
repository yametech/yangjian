package cn.ecpark.tool.agent.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 简单原生http请求工具
 *
 * @author dengliming
 * @date 2019/11/25
 */
public class HttpUtil {

    /**
     * 默认超时时间
     */
    private static final int DEFAULT_TIME_OUT = 1000 * 5;

    public static String doGet(String url) throws IOException {
        return doGet(url, DEFAULT_TIME_OUT, DEFAULT_TIME_OUT);
    }

    public static String doGet(String url, int connectTimeout, int readTimeout) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setRequestMethod("GET");
            conn.connect();
            int statusCode = conn.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                return response.toString();
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(doGet("http://www.baidu.com"));
    }
}
