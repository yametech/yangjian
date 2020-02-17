package cn.ecpark.tool.agent.plugin.okhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.util.concurrent.TimeUnit;

public class Test {

    @org.junit.Test
    public void test() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder().url("http://www.baidu.com?a=0&b=3").build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                System.out.println(responseBody.string());
            }
            TimeUnit.SECONDS.sleep(3);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }


}
