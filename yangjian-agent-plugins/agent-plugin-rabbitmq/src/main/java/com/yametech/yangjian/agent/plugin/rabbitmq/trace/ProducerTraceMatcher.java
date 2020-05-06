package com.yametech.yangjian.agent.plugin.rabbitmq.trace;

import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.configmatch.*;
import com.yametech.yangjian.agent.api.trace.ITraceMatcher;
import com.yametech.yangjian.agent.api.trace.TraceType;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2020/4/30
 */
public class ProducerTraceMatcher implements ITraceMatcher {

    @Override
    public TraceType type() {
        return TraceType.MQ_PUBLISH;
    }

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("com.rabbitmq.client.impl.ChannelN"),
                new MethodNameMatch("basicPublish"),
                new MethodArgumentIndexMatch(4, "com.rabbitmq.client.AMQP$BasicProperties")
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
        return new LoadClassKey("com.yametech.yangjian.agent.plugin.rabbitmq.trace.ProducerSpanCreater");
    }
}
