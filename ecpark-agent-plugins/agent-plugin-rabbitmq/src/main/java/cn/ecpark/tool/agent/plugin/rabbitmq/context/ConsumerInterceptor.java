package cn.ecpark.tool.agent.plugin.rabbitmq.context;

import java.lang.reflect.Method;
import java.util.Map;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.bean.BeforeResult;
import cn.ecpark.tool.agent.api.interceptor.IMethodAOP;
import cn.ecpark.tool.agent.plugin.rabbitmq.bean.MqInfo;

public class ConsumerInterceptor implements IMethodAOP<Object> {
	
	@Override
	public BeforeResult<Object> before(Object thisObj, Object[] allArguments, Method method) {
		// 有重载方法，会导致重复执行，所以此处要判断是否存在，不用重复设置，match中不拦截具体类方法是考虑实现类可能调用重载方法中的热议一个，所以都要拦截
		if(!(allArguments[6] instanceof IContext) || !(thisObj instanceof IContext) || !(allArguments[0] instanceof String)) {
			return null;
		}
		MqInfo info = (MqInfo) ((IContext) thisObj)._getAgentContext(ContextConstants.RABBITMQ_CONTEXT_KEY);
		if(info == null) {
			return null;
		}
		MqInfo thisInfo = info.copy();
		thisInfo.setConsumeGroup((String) allArguments[0]);
		((IContext)allArguments[6])._setAgentContext(ContextConstants.RABBITMQ_CONTEXT_KEY, thisInfo);
		return null;
	}

	@Override
	public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult<Object> beforeResult,
			Object ret, Throwable t, Map<Class<?>, Object> globalVar) {
		return ret;
	}
	
}