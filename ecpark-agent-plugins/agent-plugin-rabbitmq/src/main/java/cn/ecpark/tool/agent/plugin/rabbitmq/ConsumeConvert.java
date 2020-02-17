package cn.ecpark.tool.agent.plugin.rabbitmq;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.common.StringUtil;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;
import cn.ecpark.tool.agent.plugin.rabbitmq.bean.MqInfo;
import cn.ecpark.tool.agent.plugin.rabbitmq.context.ContextConstants;
import com.rabbitmq.client.Envelope;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 输出kafka Qps数量
 *
 * @author liuzhao
 * @Description
 * @date 2019年11月6日 下午8:07:04
 */
public class ConsumeConvert implements IMethodConvert {
	
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

        if (StringUtil.isEmpty(mqInfo.getTopic())) {
            mqInfo.setTopic(getExchange(allArguments[1]));
        }

        TimeEvent event = get(startTime);
		event.setIdentify(mqInfo.getTopic() + Constants.IDENTIFY_SEPARATOR + mqInfo.getConsumeGroup());
		return Arrays.asList(event);
    }

    private String getExchange(Object object) {
	    if (object instanceof Envelope) {
	        return ((Envelope) object).getExchange();
        }
	    return null;
    }
}
