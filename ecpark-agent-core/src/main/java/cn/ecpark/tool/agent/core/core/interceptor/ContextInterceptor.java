package cn.ecpark.tool.agent.core.core.interceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.ecpark.tool.agent.core.core.agent.IContextField;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * 方法名字一定不能改
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月14日 下午11:45:29
 */
public class ContextInterceptor {
	
	@RuntimeType
	public static Object _getAgentContext(String key, @This IContextField thisObj) {
		if(thisObj == null || thisObj.__getAgentContext() == null) {
    		return null;
    	}
    	return thisObj.__getAgentContext().get(key);
	}

	@RuntimeType
	public static void _setAgentContext(String key, Object value, @This IContextField thisObj) {
		if(thisObj == null) {
    		return;
    	}
		Map<String, Object> data = thisObj.__getAgentContext();
		if(data == null) {
			data = new ConcurrentHashMap<>();
			thisObj.__setAgentContext(data);
		}
		data.put(key, value);
	}

}
