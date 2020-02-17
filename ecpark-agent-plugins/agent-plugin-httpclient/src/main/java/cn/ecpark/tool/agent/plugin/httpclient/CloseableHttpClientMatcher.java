package cn.ecpark.tool.agent.plugin.httpclient;

import java.util.Arrays;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.configmatch.ClassAnnotationMatch;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineOrMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentNumMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodNameMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodReturnMatch;
import cn.ecpark.tool.agent.api.configmatch.NotMatch;
import cn.ecpark.tool.agent.api.configmatch.SuperClassMatch;

/**
 * 转换httpclient调用事件
 * <p>
 * 支持版本：4.0.x-4.5.x
 *
 * @author dengliming
 * @date 2019/11/21
 */
public class CloseableHttpClientMatcher implements IMetricMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineOrMatch(Arrays.asList(
                // 主要是兼容4.0.x-4.2.x版本，4.3版本该类已弃用
                new CombineAndMatch(Arrays.asList(
                        new ClassMatch("org.apache.http.impl.client.DefaultRequestDirector"),
                        new NotMatch(new ClassAnnotationMatch("java.lang.Deprecated")),
                        new MethodNameMatch("execute"),
                        new MethodArgumentNumMatch(3))),

                new CombineAndMatch(Arrays.asList(
                        new SuperClassMatch("org.apache.http.impl.client.CloseableHttpClient"),
                        new MethodNameMatch("doExecute"),
                        new MethodArgumentNumMatch(3),
                        new MethodReturnMatch("org.apache.http.client.methods.CloseableHttpResponse")))
        ));
    }

    @Override
    public String type() {
        return Constants.EventType.HTTP_CLIENT;
    }
    
    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.httpclient.CloseableHttpClientConvert");
    }

}
