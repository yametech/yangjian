package cn.ecpark.tool.agent.plugin.mongo.context;

import java.util.Arrays;

import cn.ecpark.tool.agent.api.IEnhanceClassMatch;
import cn.ecpark.tool.agent.api.InterceptorMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineOrMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentIndexMatch;

/**
 * MongoDb集合操作类增强获取集合名称
 *
 * @author dengliming
 * @date 2019/12/17
 */
public class MongoOperationMatcher implements InterceptorMatcher, IEnhanceClassMatch {

    @Override
    public IConfigMatch classMatch() {
        return new CombineOrMatch(Arrays.asList(
                new ClassMatch("com.mongodb.operation.CountOperation"),
                new ClassMatch("com.mongodb.operation.AggregateOperation")
        ));
    }

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(classMatch(),
                new MethodArgumentIndexMatch(0, "com.mongodb.MongoNamespace")
        ));
    }
    
    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.mongo.context.MongoOperationInterceptor");
    }

}
