package com.yametech.yangjian.agent.plugin.client.metric;

import com.yametech.yangjian.agent.api.IMetricMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.configmatch.MethodAnnotationMatch;

public class MetricAnnotationMatcher implements IMetricMatcher {

    @Override
    public IConfigMatch match() {
        return new MethodAnnotationMatch("com.yametech.yangjian.agent.client.annotation.Metric");
    }

    @Override
    public String type() {
        return Constants.EventType.METHOD;
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
        return new LoadClassKey("com.yametech.yangjian.agent.plugin.client.metric.MetricAnnotationConvert");
    }

}