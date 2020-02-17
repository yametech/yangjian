package cn.ecpark.tool.agent.plugin.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpClientTest {

    /**
     * 4.1
     */
    @org.junit.Test
    public void test() {
        String url = "https://www.cnblogs.com/liliHexiaogou/p/11478602.html";
        HttpPost post = null;
        try {
            HttpClient client = new DefaultHttpClient();
            post = new HttpPost(url);
            post.setHeader(HTTP.CONTENT_TYPE, "application/json; charset=UTF-8");
            post.setHeader("Accept", "application/json; charset=UTF-8");
            StringEntity entity = new StringEntity("", "UTF-8");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            int rspCode = response.getStatusLine().getStatusCode();
            System.out.println("rspCode:" + rspCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

	@org.junit.Test
    public void test1() {
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://fanyi.youdao.com/openapi.do");
        try {
            List<BasicNameValuePair> list = new ArrayList<>();

            list.add(new BasicNameValuePair("keyfrom", "siwuxie095-test"));

            list.add(new BasicNameValuePair("key", "2140200403"));

            list.add(new BasicNameValuePair("type", "data"));

            list.add(new BasicNameValuePair("doctype", "xml"));

            list.add(new BasicNameValuePair("version", "1.1"));

            list.add(new BasicNameValuePair("q", "welcome"));

            post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));


            HttpResponse response = client.execute(post);

            HttpEntity entity = response.getEntity();


            String result = EntityUtils.toString(entity, "UTF-8");


            System.out.println(result);


        } catch (ClientProtocolException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

}
