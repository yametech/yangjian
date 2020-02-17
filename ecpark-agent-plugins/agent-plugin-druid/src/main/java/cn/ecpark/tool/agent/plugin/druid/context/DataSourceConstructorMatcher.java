package cn.ecpark.tool.agent.plugin.druid.context;

import cn.ecpark.tool.agent.api.IEnhanceClassMatch;
import cn.ecpark.tool.agent.api.InterceptorMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentIndexMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentNumMatch;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public class DataSourceConstructorMatcher implements InterceptorMatcher, IEnhanceClassMatch {

    @Override
    public IConfigMatch classMatch() {
        return new ClassMatch("com.alibaba.druid.pool.DruidDataSource");
    }

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                classMatch(),
                new MethodArgumentNumMatch(1),
                new MethodArgumentIndexMatch(0, "boolean")
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
        return new LoadClassKey("cn.ecpark.tool.agent.plugin.druid.context.DataSourceConstructorInterceptor");
    }
}
