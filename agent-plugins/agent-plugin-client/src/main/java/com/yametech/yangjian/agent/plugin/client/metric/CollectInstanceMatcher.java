package com.yametech.yangjian.agent.plugin.client.metric;

import java.util.Arrays;

import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.configmatch.CombineAndMatch;
import com.yametech.yangjian.agent.api.configmatch.InterfaceMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodConstructorMatch;

public class CollectInstanceMatcher implements InterceptorMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new InterfaceMatch("com.yametech.yangjian.agent.client.IStatusCollect"),
                new MethodConstructorMatch()
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
        return new LoadClassKey("com.yametech.yangjian.agent.plugin.client.metric.CollectInstanceInterceptor");
    }
}