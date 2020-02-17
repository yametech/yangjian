package cn.ecpark.tool.agent.plugin.mongo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;
import cn.ecpark.tool.agent.plugin.mongo.util.MongoUtil;

/**
 * mongo集合操作方法拦截
 *
 * 支持版本：3.8.x-3.12.0
 * com.mongodb.client.internal.MongoClientDelegate$DelegateOperationExecutor.execute(...)
 *
 * @author dengliming
 * @date 2019/12/13
 */
public class DelegateOperationExecutorConvert implements IMethodConvert {
	
	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
		return MongoUtil.buildRTEvent(startTime, allArguments[0]);
	}

}
