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
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentNumMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodRegexMatch;

/**
 * 增强类为了获取StatementImpl执行的sql（主要原因是因为执行executeBatch之后jdbc会把batchArgs清理掉导致拿不到sql）
 *
 * @author dengliming
 * @date 2019/11/27
 */
public class StatementImplMatcher implements InterceptorMatcher, IEnhanceClassMatch {

    @Override
    public IConfigMatch classMatch() {
        return new CombineOrMatch(Arrays.asList(
                new ClassMatch("com.mysql.jdbc.StatementImpl"),
                new ClassMatch("com.mysql.cj.jdbc.StatementImpl")
        ));
    }

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new MethodRegexMatch(".*com\\.mysql(\\.cj)?\\.jdbc\\.StatementImpl\\.addBatch\\(.*"),
                new MethodArgumentNumMatch(1),
                new MethodArgumentIndexMatch(0, "java.lang.String"))
        );
    }
    
    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("cn.ecpark.tool.agent.plugin.mysql.context.StatementImplInterceptor");
    }
    
}
