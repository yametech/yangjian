package cn.ecpark.tool.agent.plugin.spring;

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
 * 转换spring controller事件
 *
 * @author liuzhao
 * @Description
 * @date 2019年10月9日 下午3:43:15
 */
public class ControllerMatcher implements IMetricMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
//                new SuperClassMatch("org.springframework.web.method.HandlerMethod"),
                new ClassMatch("org.springframework.web.method.support.InvocableHandlerMethod"),
                new MethodNameMatch("doInvoke")
        ));
    }

    @Override
    public String type() {
        return Constants.EventType.HTTP_SERVER;
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.spring.ControllerConvert");
    }
}
