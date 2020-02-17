package cn.ecpark.tool.agent.plugin.rabbitmq.context;

import java.lang.reflect.Method;
import java.util.Map;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.bean.BeforeResult;
import cn.ecpark.tool.agent.api.interceptor.IMethodAOP;
import cn.ecpark.tool.agent.plugin.rabbitmq.bean.MqInfo;

/**
 * 添加exchange上下文
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月8日 下午6:13:04
 */
public class ExchangeInterceptor implements IMethodAOP<Object> {
	
	@Override
	public BeforeResult<Object> before(Object thisObj, Object[] allArguments, Method method) {
		return null;
	}

	@Override
	public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult<Object> beforeResult,
			Object ret, Throwable t, Map<Class<?>, Object> globalVar) {
		if(!(thisObj instanceof IContext) || allArguments == null || allArguments.length == 0 || allArguments[0] == null) {
			return ret;
		}
		MqInfo info = (MqInfo) ((IContext)thisObj)._getAgentContext(ContextConstants.RABBITMQ_CONTEXT_KEY);
		// 有重载方法，会导致重复执行，所以此处要判断是否存在，不用重复设置，match中不拦截具体类方法是考虑实现类可能调用重载方法中的热议一个，所以都要拦截
		if(info == null || info.getTopic() != null) {
			return ret;
		}
		info.setTopic((String) allArguments[0]);
		return ret;
	}
	
}
