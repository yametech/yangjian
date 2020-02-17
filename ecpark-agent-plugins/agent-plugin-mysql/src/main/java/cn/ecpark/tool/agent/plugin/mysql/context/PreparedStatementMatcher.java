package cn.ecpark.tool.agent.plugin.mysql.context;

import java.util.Arrays;

import cn.ecpark.tool.agent.api.IEnhanceClassMatch;
import cn.ecpark.tool.agent.api.InterceptorMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineOrMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentIndexMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodRegexMatch;

/**
 * 增强类为了获取jdbcPreparedStatement执行的sql
 *
 * @author dengliming
 * @date 2019/11/27
 */
public class PreparedStatementMatcher implements InterceptorMatcher, IEnhanceClassMatch {

    @Override
    public IConfigMatch classMatch() {
        return new CombineOrMatch(Arrays.asList(
                // 5.x
                new ClassMatch("com.mysql.jdbc.PreparedStatement"),
                // 8.x
                new ClassMatch("com.mysql.cj.jdbc.ClientPreparedStatement"),
                // 6.x
                new ClassMatch("com.mysql.cj.jdbc.PreparedStatement"),
                // useServerPrepStmts=true时用到
                new ClassMatch("com.mysql.jdbc.ServerPreparedStatement"),
                new ClassMatch("com.mysql.cj.jdbc.ServerPreparedStatement")));
    }

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new MethodRegexMatch(".*com\\.mysql(\\.cj)?\\.jdbc\\..*PreparedStatement\\(.*"),
                new MethodArgumentIndexMatch(1, "java.lang.String"))
        );
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.mysql.context.PreparedStatementInterceptor");
    }
}
