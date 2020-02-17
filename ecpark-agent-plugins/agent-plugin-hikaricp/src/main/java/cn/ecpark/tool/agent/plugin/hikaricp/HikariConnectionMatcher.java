package cn.ecpark.tool.agent.plugin.hikaricp;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodNameMatch;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2019/12/22
 */
public class HikariConnectionMatcher implements IMetricMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("com.zaxxer.hikari.HikariDataSource"),
                new MethodNameMatch("getConnection")
        ));
    }

    @Override
    public String type() {
        return Constants.EventType.HIKARICP;
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
        return new LoadClassKey("cn.ecpark.tool.agent.plugin.hikaricp.HikariConnectionConvert");
    }
}
