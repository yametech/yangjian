package cn.ecpark.tool.agent.plugin.kafka.context;

import java.util.Arrays;

import cn.ecpark.tool.agent.api.InterceptorMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentNumMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodNameMatch;

/**
 * 增强类定义
 *
 * @author liuzhao
 * @Description
 * @date 2019年11月8日 下午6:13:04
 */
public class ConsumeTimeMatcher implements InterceptorMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("org.apache.kafka.clients.consumer.KafkaConsumer"),
                new MethodNameMatch("poll"),
                new MethodArgumentNumMatch(2)
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.kafka.context.ConsumeTimeInterceptor");
    }
    
    @Override
    public int weight() {
        return 99;
    }
}
