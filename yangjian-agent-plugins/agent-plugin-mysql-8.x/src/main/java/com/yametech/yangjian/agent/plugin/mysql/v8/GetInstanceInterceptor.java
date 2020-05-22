package com.yametech.yangjian.agent.plugin.mysql.v8;

import com.mysql.cj.conf.HostInfo;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.interceptor.IStaticMethodAOP;
import com.yametech.yangjian.agent.plugin.mysql.commons.context.ContextConstants;
import com.yametech.yangjian.agent.plugin.mysql.commons.util.MysqlUtil;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author dengliming
 * @date 2020/5/17
 */
public class GetInstanceInterceptor implements IStaticMethodAOP {

    @Override
    public BeforeResult before(Object[] allArguments, Method method) throws Throwable {
        return null;
    }

    @Override
    public Object after(Object[] allArguments, Method method, BeforeResult beforeResult, Object ret, Throwable t, Map globalVar) throws Throwable {
        if (ret instanceof IContext) {
            final HostInfo hostInfo = (HostInfo) allArguments[0];
            ((IContext) ret)._setAgentContext(ContextConstants.MYSQL_CONNECTION_INFO_CONTEXT_KEY, MysqlUtil.getMysqlConnectionInfo(hostInfo.getDatabaseUrl()));
        }
        return ret;
    }
}
