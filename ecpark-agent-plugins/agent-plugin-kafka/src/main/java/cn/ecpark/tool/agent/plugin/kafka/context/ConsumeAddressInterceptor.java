package cn.ecpark.tool.agent.plugin.kafka.context;

import java.util.Properties;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.interceptor.IConstructorListener;
import cn.ecpark.tool.agent.plugin.kafka.bean.MqInfo;

/**
 * 增强类定义
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月8日 下午6:13:04
 */
public class ConsumeAddressInterceptor implements IConstructorListener {
	
	@Override
	public void constructor(Object thisObj, Object[] allArguments) {
		if(!(thisObj instanceof IContext) || allArguments == null || allArguments[0] == null) {
			return;
		}
		Properties prop = (Properties) allArguments[0];
		MqInfo info = new MqInfo(prop.getProperty("bootstrap.servers"), null, prop.getProperty("group.id"));
		((IContext)thisObj)._setAgentContext(ContextConstants.KAFKA_CONTEXT_KEY, info);
	}
}
