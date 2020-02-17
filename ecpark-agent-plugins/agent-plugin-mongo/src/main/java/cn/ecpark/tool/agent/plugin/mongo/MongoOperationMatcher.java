package cn.ecpark.tool.agent.plugin.mongo;

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
 * mongo集合操作方法拦截
 *
 * 支持版本：3.0.x~3.5.x
 * com.mongodb.Mongo#execute(...)
 *
 * @author dengliming
 * @date 2019/12/13
 */
public class MongoOperationMatcher implements IMetricMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("com.mongodb.Mongo"),
                new MethodNameMatch("execute")
        ));
    }

    @Override
    public String type() {
        return Constants.EventType.MONGO;
    }
    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.mongo.MongoOperationConvert");
    }
    
}
