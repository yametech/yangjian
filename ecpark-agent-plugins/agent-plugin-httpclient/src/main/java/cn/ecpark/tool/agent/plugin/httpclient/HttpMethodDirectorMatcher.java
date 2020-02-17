package cn.ecpark.tool.agent.plugin.httpclient;

import java.util.Arrays;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentNumMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodNameMatch;

/**
 * 转换httpclient调用事件
 * 支持版本：httpclient-3、httpclient-3.1
 *
 * @author dengliming
 * @date 2019/11/21
 */
public class HttpMethodDirectorMatcher implements IMetricMatcher {
    @Override
    public IConfigMatch match() {
        // 3.x
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("org.apache.commons.httpclient.HttpMethodDirector"),
                new MethodNameMatch("executeMethod"),
                new MethodArgumentNumMatch(1)));
    }

    @Override
    public String type() {
        return Constants.EventType.HTTP_CLIENT;
    }
    
    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.httpclient.HttpMethodDirectorConvert");
    }
    
}
