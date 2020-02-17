package cn.ecpark.tool.agent.plugin.kafka;

import java.util.Arrays;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentIndexMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentNumMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodNameMatch;

/**
 * 输出kafka Qps数量
 *
 * @author liuzhao
 * @Description
 * @date 2019年11月6日 下午8:07:04
 */
public class KafkaPublishMatcher implements IMetricMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("org.apache.kafka.clients.producer.KafkaProducer"),
                new MethodNameMatch("send"),
                new MethodArgumentNumMatch(2),
                new MethodArgumentIndexMatch(0, "org.apache.kafka.clients.producer.ProducerRecord"),
                new MethodArgumentIndexMatch(1, "org.apache.kafka.clients.producer.Callback")
        ));
    }

    @Override
    public String type() {
        return Constants.EventType.KAFKA_PUBLISH;
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.kafka.KafkaPublishConvert");
    }
}
