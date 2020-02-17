package cn.ecpark.tool.agent.plugin.kafka;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerRecord;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;
import cn.ecpark.tool.agent.plugin.kafka.bean.MqInfo;
import cn.ecpark.tool.agent.plugin.kafka.context.ContextConstants;

/**
 * 输出kafka Qps数量
 *
 * @author liuzhao
 * @Description
 * @date 2019年11月6日 下午8:07:04
 */
public class KafkaPublishConvert implements IMethodConvert {
	
	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        if (!(thisObj instanceof IContext) || allArguments[0] == null) {
            return null;
        }
        MqInfo mqInfo = (MqInfo) ((IContext) thisObj)._getAgentContext(ContextConstants.KAFKA_CONTEXT_KEY);
        if (mqInfo == null) {
            return null;
        }
        ProducerRecord<?, ?> record = (ProducerRecord<?, ?>) allArguments[0];
        TimeEvent event = get(startTime);
		event.setIdentify(record.topic());
		return Arrays.asList(event);
    }
}
