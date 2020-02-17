package cn.ecpark.tool.agent.plugin.kafka;

import java.util.Arrays;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentNumMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodNameMatch;

/**
 * Kafka消费方法拦截RT统计
 *
 * @author dengliming
 * @date 2019/12/16
 */
public class KafkaRTEventMatcher implements IMetricMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("org.apache.kafka.clients.consumer.KafkaConsumer"),
                new MethodNameMatch("poll"),
                new MethodArgumentNumMatch(2)
        ));
    }

    @Override
    public String type() {
        return Constants.EventType.KAFKA_CONSUME;
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.kafka.KafkaRTEventConvert");
    }
    
}
