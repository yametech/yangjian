package cn.ecpark.tool.agent.plugin.dubbo;

import java.util.Arrays;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineOrMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentIndexMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentNumMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodRegexMatch;

/**
 * 将dubbo服务端调用，转换成实际调用的接口
 * 支持版本：
 * alibaba：dubbo-2.4.10、dubbo-2.5.3、dubbo-2.5.4、dubbo-2.5.5、dubbo-2.5.6、dubbo-2.5.7、dubbo-2.5.10、dubbo-2.6.0、dubbo-2.6.1、dubbo-2.6.2、dubbo-2.6.3、dubbo-2.6.4、dubbo-2.6.5、dubbo-2.6.6、dubbo-2.6.7、dubbo-2.8.3、dubbo-2.8.4
 * apache：dubbo-2.7.0、dubbo-2.7.1、dubbo-2.7.2、dubbo-2.7.3、dubbo-2.7.4
 *
 * @author liuzhao
 * @Description
 * @date 2019年10月9日 下午3:43:15
 */
public class DubboServerMatcher implements IMetricMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new CombineOrMatch(Arrays.asList(
                        new MethodRegexMatch(".*com\\.alibaba\\.dubbo\\.rpc\\.proxy\\.javassist\\.JavassistProxyFactory.*doInvoke\\(.*"),
                        new MethodRegexMatch(".*org\\.apache\\.dubbo\\.rpc\\.proxy\\.javassist\\.JavassistProxyFactory.*doInvoke\\(.*")
                )),
                new MethodArgumentNumMatch(4),
                new MethodArgumentIndexMatch(1, "java.lang.String")
        ));
    }

    @Override
    public String type() {
        return Constants.EventType.DUBBO_SERVER;
    }
    
    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.dubbo.DubboServerConvert");
    }
    
}
