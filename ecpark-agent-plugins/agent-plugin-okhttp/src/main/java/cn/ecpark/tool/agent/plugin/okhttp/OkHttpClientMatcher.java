package cn.ecpark.tool.agent.plugin.okhttp;

import java.util.Arrays;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodNameMatch;

/**
 * 转换httpclient调用事件
 * <p>
 * 支持版本：okhttp-3.x
 *
 * @author dengliming
 * @date 2019/11/22
 */
public class OkHttpClientMatcher implements IMetricMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("okhttp3.RealCall"),
                new MethodNameMatch("execute")));
    }

    @Override
    public String type() {
        return Constants.EventType.HTTP_CLIENT;
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.okhttp.OkHttpClientConvert");
    }
}
