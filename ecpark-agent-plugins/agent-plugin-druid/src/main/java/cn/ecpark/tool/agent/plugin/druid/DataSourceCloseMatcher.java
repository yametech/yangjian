package cn.ecpark.tool.agent.plugin.druid;

import cn.ecpark.tool.agent.api.InterceptorMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodNameMatch;
import cn.ecpark.tool.agent.core.datasource.DataSourceMonitorRegistry;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public class DataSourceCloseMatcher implements InterceptorMatcher {

    private final DataSourceMonitorRegistry dataSourceMonitorRegistry = DataSourceMonitorRegistry.INSTANCE;

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("com.alibaba.druid.pool.DruidDataSource"),
                new MethodNameMatch("close")
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
        return new LoadClassKey("cn.ecpark.tool.agent.plugin.druid.DataSourceCloseInterceptor");
    }
}
