package com.yametech.yangjian.agent.plugin.client.metric;

import com.yametech.yangjian.agent.api.IMetricMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.configmatch.*;

import java.util.Arrays;

public class MethodMetricMatcher implements IMetricMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("com.yametech.yangjian.agent.client.MetricUtil"),
                new MethodNameMatch("mark"),
                new MethodArgumentNumMatch(2),
                new MethodArgumentIndexMatch(0, "java.lang.String"),
                new MethodArgumentIndexMatch(1, "java.util.function.Supplier")
        ));
    }

    @Override
    public String type() {
        return Constants.EventType.METHOD;
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
        return new LoadClassKey("com.yametech.yangjian.agent.plugin.client.metric.MethodMetricConvert");
    }

}