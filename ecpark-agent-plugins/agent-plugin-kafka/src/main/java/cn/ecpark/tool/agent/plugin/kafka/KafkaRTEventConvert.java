package cn.ecpark.tool.agent.plugin.kafka;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.convert.IMethodBeforeConvert;
import cn.ecpark.tool.agent.plugin.kafka.bean.KafkaConsumeRT;
import cn.ecpark.tool.agent.plugin.kafka.bean.MqInfo;
import cn.ecpark.tool.agent.plugin.kafka.context.ContextConstants;

/**
 * Kafka消费方法拦截RT统计
 *
 * @author dengliming
 * @date 2019/12/16
 */
public class KafkaRTEventConvert implements IMethodBeforeConvert {

    @Override
    public List<TimeEvent> convert(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        if (!(thisObj instanceof IContext)) {
            return null;
        }
        MqInfo mqInfo = (MqInfo) ((IContext) thisObj)._getAgentContext(ContextConstants.KAFKA_CONTEXT_KEY);
        if (mqInfo == null) {
            return null;
        }
        KafkaConsumeRT consumeRT = (KafkaConsumeRT) ((IContext) thisObj)._getAgentContext(ContextConstants.KAFKA_CONSUME_INFO);
        if (consumeRT == null) {
            return null;
        }

        int totalNum = consumeRT.getTopicNum().values().stream().mapToInt(a -> a).sum();
        long currentTime = System.currentTimeMillis();
        long useTime = currentTime - consumeRT.getStartTime();
        List<TimeEvent> timeEvents = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : consumeRT.getTopicNum().entrySet()) {
            TimeEvent timeEvent = new TimeEvent();
            timeEvent.setType(Constants.EventType.KAFKA_CONSUME);
            timeEvent.setIdentify(entry.getKey() + Constants.IDENTIFY_SEPARATOR + mqInfo.getConsumeGroup());
            timeEvent.setNumber(entry.getValue());
            timeEvent.setUseTime(entry.getValue() / totalNum * useTime);
            // 事件统计目前只保留最近1<<5秒统计，eventTime取当前时间、防止方法执行时间太长导致丢弃
            timeEvent.setEventTime(currentTime);
            timeEvents.add(timeEvent);
        }
        return timeEvents;
    }
}
