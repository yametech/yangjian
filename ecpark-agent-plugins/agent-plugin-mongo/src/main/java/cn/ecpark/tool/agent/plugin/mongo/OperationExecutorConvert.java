package cn.ecpark.tool.agent.plugin.mongo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;
import cn.ecpark.tool.agent.plugin.mongo.util.MongoUtil;

/**
 * mongo集合操作方法拦截
 * 支持版本：3.6.x
 * com.mongodb.OperationExecutor.execute(...)
 *
 * @author dengliming
 * @date 2019/12/13
 */
public class OperationExecutorConvert implements IMethodConvert {
	
	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        if (allArguments == null || allArguments.length == 0) {
            return null;
        }
        return MongoUtil.buildRTEvent(startTime, allArguments[0]);
    }

}
