package cn.ecpark.tool.agent.plugin.rabbitmq;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;
import cn.ecpark.tool.agent.plugin.rabbitmq.bean.MqInfo;
import cn.ecpark.tool.agent.plugin.rabbitmq.context.ContextConstants;

/**
 *
 * @author liuzhao
 * @Description
 * @date 2019年11月6日 下午8:07:04
 */
public class PublishConvert implements IMethodConvert {
	
	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        if (!(thisObj instanceof IContext)) {
            return null;
        }
        MqInfo mqInfo = (MqInfo) ((IContext) thisObj)._getAgentContext(ContextConstants.RABBITMQ_CONTEXT_KEY);
        if (mqInfo == null) {
            return null;
        }
        String topic = mqInfo.getTopic();
        if (allArguments != null && allArguments.length > 0 && allArguments[0] != null) {
            topic = (String) allArguments[0];
        }
        TimeEvent event = get(startTime);
		event.setIdentify(topic);
		return Arrays.asList(event);
    }


}
