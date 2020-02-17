package cn.ecpark.tool.agent.plugin.hikaricp.context;

import cn.ecpark.tool.agent.api.IEnhanceClassMatch;
import cn.ecpark.tool.agent.api.InterceptorMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.configmatch.*;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2019/12/21
 */
public class HikariPoolConstructorMatcher implements InterceptorMatcher, IEnhanceClassMatch {

    @Override
    public IConfigMatch classMatch() {
        return new ClassMatch("com.zaxxer.hikari.pool.HikariPool");
    }

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                classMatch(),
                new MethodArgumentIndexMatch(0, "com.zaxxer.hikari.HikariConfig"),
                new CombineOrMatch(
                        Arrays.asList(
                                // 2.3.4~
                                new CombineAndMatch(Arrays.asList(
                                        new MethodArgumentNumMatch(1))),
                                // 2.3.4
                                new CombineAndMatch(Arrays.asList(
                                        new MethodArgumentIndexMatch(1, "java.lang.String"),
                                        new MethodArgumentIndexMatch(2, "java.lang.String"),
                                        new MethodArgumentNumMatch(3))))
                )
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
        return new LoadClassKey("cn.ecpark.tool.agent.plugin.hikaricp.context.HikariPoolConstructorInterceptor");
    }
}
