package cn.ecpark.tool.agent.plugin.kafka.context;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.bean.BeforeResult;
import cn.ecpark.tool.agent.api.interceptor.IMethodAOP;
import cn.ecpark.tool.agent.plugin.kafka.bean.KafkaConsumeRT;

/**
 * 增强类定义
 *
 * @author liuzhao
 * @Description
 * @date 2019年11月8日 下午6:13:04
 */
public class ConsumeTimeInterceptor implements IMethodAOP<Object> {

    @Override
    public BeforeResult<Object> before(Object thisObj, Object[] allArguments, Method method) {
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult<Object> beforeResult,
                        Object ret, Throwable t, Map<Class<?>, Object> globalVar) {
        if (!(thisObj instanceof IContext)) {
            return ret;
        }
        IContext context = (IContext) thisObj;
        KafkaConsumeRT consumeRT = (KafkaConsumeRT) context._getAgentContext(ContextConstants.KAFKA_CONSUME_INFO);
        ConsumerRecords<?, ?> records = (ConsumerRecords<?, ?>) ret;
        if (consumeRT == null) {
            consumeRT = new KafkaConsumeRT();
            context._setAgentContext(ContextConstants.KAFKA_CONSUME_INFO, consumeRT);
        }
        consumeRT.setTopicNum(getTopicNum(records));
        consumeRT.setStartTime(System.currentTimeMillis());
        return ret;
    }

    private Map<String, Integer> getTopicNum(ConsumerRecords<?, ?> records) {
        if (records == null) {
            return null;
        }
        Map<String, Integer> topicNum = new HashMap<>();
        for (ConsumerRecord<?, ?> record : records) {
            Integer num = topicNum.getOrDefault(record.topic(), 0);
            topicNum.put(record.topic(), num + 1);
        }
        return topicNum;
    }

}
