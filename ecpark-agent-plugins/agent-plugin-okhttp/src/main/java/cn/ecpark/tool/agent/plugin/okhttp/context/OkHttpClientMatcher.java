package cn.ecpark.tool.agent.plugin.okhttp.context;

import cn.ecpark.tool.agent.api.IEnhanceClassMatch;
import cn.ecpark.tool.agent.api.InterceptorMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentIndexMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodRegexMatch;

import java.util.Arrays;

/**
 * 增强类为了获取http请求url
 *
 * @author dengliming
 * @date 2019/11/22
 */
public class OkHttpClientMatcher implements InterceptorMatcher, IEnhanceClassMatch {

    @Override
    public IConfigMatch classMatch() {
        return new ClassMatch("okhttp3.RealCall");
    }

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new MethodRegexMatch(".*okhttp[3]\\.RealCall\\(.*"),
                new MethodArgumentIndexMatch(1, "okhttp3.Request")));
    }
    
    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.okhttp.context.OkHttpClientInterceptor");
    }

}
