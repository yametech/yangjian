package cn.ecpark.tool.agent.plugin.mongo;

import java.util.Arrays;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineOrMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentIndexMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodNameMatch;

/**
 * mongo集合操作方法拦截
 *
 * 支持版本：3.8.x-3.12.0
 * com.mongodb.client.internal.MongoClientDelegate$DelegateOperationExecutor.execute(...)
 *
 * @author dengliming
 * @date 2019/12/13
 */
public class DelegateOperationExecutorMatcher implements IMetricMatcher {

    // 3.8.x-3.12.0
    private static final String ARGUMENT_TYPE_1 = "com.mongodb.client.ClientSession";
    // 3.7.x
    private static final String ARGUMENT_TYPE_2 = "com.mongodb.session.ClientSession";
    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("com.mongodb.client.internal.MongoClientDelegate$DelegateOperationExecutor"),
                new MethodNameMatch("execute"),

                new CombineOrMatch(Arrays.asList(
                        new MethodArgumentIndexMatch(2, ARGUMENT_TYPE_1),
                        new MethodArgumentIndexMatch(3, ARGUMENT_TYPE_1),
                        new MethodArgumentIndexMatch(1, ARGUMENT_TYPE_2),
                        new MethodArgumentIndexMatch(2, ARGUMENT_TYPE_2)
                ))
        ));
    }

    @Override
    public String type() {
        return Constants.EventType.MONGO;
    }
    
    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.mongo.DelegateOperationExecutorConvert");
    }
}
