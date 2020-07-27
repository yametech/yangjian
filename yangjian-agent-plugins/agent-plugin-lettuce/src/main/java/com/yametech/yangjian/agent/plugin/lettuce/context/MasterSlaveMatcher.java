package com.yametech.yangjian.agent.plugin.lettuce.context;

import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.configmatch.*;

import java.util.Arrays;

/**
 * support 5.1.x
 *
 * @author dengliming
 * @date 2020/7/21
 */
public class MasterSlaveMatcher implements InterceptorMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("io.lettuce.core.masterslave.MasterSlave"),
                new MethodStatisticMatch(),
                new CombineOrMatch(Arrays.asList(
                        new MethodNameMatch("connect"),
                        new MethodNameMatch("connectAsync")
                )),
                new MethodArgumentIndexMatch(2, "java.lang.Iterable")
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
        return new LoadClassKey("com.yametech.yangjian.agent.plugin.lettuce.context.MasterSlaveInterceptor");
    }
}
