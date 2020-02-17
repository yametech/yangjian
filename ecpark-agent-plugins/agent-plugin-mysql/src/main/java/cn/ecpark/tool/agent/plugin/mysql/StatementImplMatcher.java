package cn.ecpark.tool.agent.plugin.mysql;

import java.util.Arrays;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineOrMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodNameMatch;

/**
 * 转换JDBC调用事件(StatementImpl)
 *
 * @author dengliming
 * @date 2019/11/25
 */
public class StatementImplMatcher implements IMetricMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new CombineOrMatch(Arrays.asList(
                        new ClassMatch("com.mysql.jdbc.StatementImpl"),
                        new ClassMatch("com.mysql.cj.jdbc.StatementImpl")
                )),
                new CombineOrMatch(Arrays.asList(
                        new MethodNameMatch("execute"),
                        new MethodNameMatch("executeQuery"),
                        new MethodNameMatch("executeUpdate"),
                        new MethodNameMatch("executeBatchInternal"),
                        new MethodNameMatch("executeUpdateInternal"),
                        new MethodNameMatch("executeLargeUpdate"),
                        new MethodNameMatch("executeBatch")))));
    }

    @Override
    public String type() {
        return Constants.EventType.MYSQL;
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.mysql.StatementImplConvert");
    }
}
