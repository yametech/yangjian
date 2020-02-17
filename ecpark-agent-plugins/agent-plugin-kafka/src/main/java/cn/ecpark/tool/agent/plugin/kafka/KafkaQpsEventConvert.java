package cn.ecpark.tool.agent.plugin.kafka;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;
import cn.ecpark.tool.agent.api.convert.statistic.StatisticType;
import cn.ecpark.tool.agent.plugin.kafka.bean.MqInfo;
import cn.ecpark.tool.agent.plugin.kafka.context.ContextConstants;

/**
 * 输出kafka Qps数量
 *
 * @author liuzhao
 * @Description
 * @date 2019年11月6日 下午8:07:04
 */
public class KafkaQpsEventConvert implements IMethodConvert {

	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        if (!(thisObj instanceof IContext)) {
            return null;
        }
        MqInfo mqInfo = (MqInfo) ((IContext) thisObj)._getAgentContext(ContextConstants.KAFKA_CONTEXT_KEY);
        if (mqInfo == null) {
            return null;
        }
        String group = mqInfo.getConsumeGroup();
        return buildQpsEvent(startTime, ret, group);
    }

    /**
     * QPS事件
     *
     * @param startTime
     * @param ret
     * @param group
     * @return
     */
    private List<TimeEvent> buildQpsEvent(long startTime, Object ret, String group) {
        if (ret == null) {
            return null;
        }
        ConsumerRecords<?, ?> records = (ConsumerRecords<?, ?>) ret;
        Map<String, TimeEvent> topicNum = new HashMap<>();
        long currentTime = System.currentTimeMillis();
        long useTime = currentTime - startTime;
        for (ConsumerRecord<?, ?> record : records) {
            TimeEvent timeEvent = topicNum.get(record.topic());
            if (timeEvent == null) {
                timeEvent = new TimeEvent();
                timeEvent.setStatisticTypes(new StatisticType[]{StatisticType.QPS});
                timeEvent.setType(Constants.EventType.KAFKA_CONSUME);
                timeEvent.setIdentify(record.topic() + Constants.IDENTIFY_SEPARATOR + group);
                timeEvent.setNumber(0);
                timeEvent.setEventTime(currentTime);
                timeEvent.setUseTime(useTime);
                topicNum.put(record.topic(), timeEvent);
            }
            timeEvent.setNumber(timeEvent.getNumber() + 1);
        }
        return new ArrayList<>(topicNum.values());
    }

}
