package cn.ecpark.tool.agent.plugin.druid;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.configmatch.*;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2019/12/22
 */
public class DruidConnectionMatcher implements IMetricMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("com.alibaba.druid.pool.DruidDataSource"),
                new MethodNameMatch("getConnection"),
                new MethodArgumentNumMatch(1),
                new MethodArgumentIndexMatch(0, "long")
        ));
    }

    @Override
    public String type() {
        return Constants.EventType.DRUID;
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
        return new LoadClassKey("cn.ecpark.tool.agent.plugin.druid.DruidConnectionConvert");
    }
}
