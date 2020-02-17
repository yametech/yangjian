package cn.ecpark.tool.agent.plugin.mongo.context;

import com.mongodb.MongoNamespace;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.interceptor.IConstructorListener;

/**
 * MongoDb集合操作类增强获取集合名称
 *
 * @author dengliming
 * @date 2019/12/17
 */
public class MongoOperationInterceptor implements IConstructorListener {

    @Override
    public void constructor(Object thisObj, Object[] allArguments) {
        if (allArguments[0] == null) {
            return;
        }
        MongoNamespace namespace = (MongoNamespace) allArguments[0];
        ((IContext) thisObj)._setAgentContext(ContextConstants.MONGO_OPERATOR_COLLECTION, namespace.getCollectionName());
    }
}
