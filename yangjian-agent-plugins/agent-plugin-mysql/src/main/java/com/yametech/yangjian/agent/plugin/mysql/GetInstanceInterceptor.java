/*
 * Copyright 2020 yametech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yametech.yangjian.agent.plugin.mysql;

import com.mysql.cj.core.conf.url.HostInfo;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.interceptor.IStaticMethodAOP;
import com.yametech.yangjian.agent.plugin.mysql.commons.bean.ConnectionInfo;
import com.yametech.yangjian.agent.plugin.mysql.commons.util.MysqlUtil;
import com.yametech.yangjian.agent.plugin.mysql.commons.context.ContextConstants;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * @author dengliming
 * @date 2020/5/17
 */
public abstract class GetInstanceInterceptor implements IStaticMethodAOP {

    /**
     * mysql5.x
     */
    public static class GetInstanceInterceptor5x extends GetInstanceInterceptor {
        @Override
        public BeforeResult before(Object[] allArguments, Method method) throws Throwable {
            return null;
        }

        @Override
        public Object after(Object[] allArguments, Method method, BeforeResult beforeResult, Object ret, Throwable t, Map globalVar) throws Throwable {
            if (ret instanceof IContext) {
                ConnectionInfo connectionInfo = MysqlUtil.getMysqlConnectionInfo(allArguments[4].toString());
                ((IContext) ret)._setAgentContext(ContextConstants.MYSQL_CONNECTION_INFO_CONTEXT_KEY, connectionInfo);
                MysqlUtil.reportDependency(connectionInfo, allArguments[4].toString());
            }
            return ret;
        }
    }

    /**
     * mysql6.x
     */
    public static class GetInstanceInterceptor6x extends GetInstanceInterceptor {
        @Override
        public BeforeResult before(Object[] allArguments, Method method) throws Throwable {
            return null;
        }

        @Override
        public Object after(Object[] allArguments, Method method, BeforeResult beforeResult, Object ret, Throwable t, Map globalVar) throws Throwable {
            if (ret instanceof IContext) {
                final HostInfo hostInfo = (HostInfo) allArguments[0];
                ConnectionInfo connectionInfo = MysqlUtil.getMysqlConnectionInfo(hostInfo.getDatabaseUrl());
                ((IContext) ret)._setAgentContext(ContextConstants.MYSQL_CONNECTION_INFO_CONTEXT_KEY, connectionInfo);
                MysqlUtil.reportDependency(connectionInfo, hostInfo.getDatabaseUrl());
            }
            return ret;
        }
    }
}
