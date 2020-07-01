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
package com.yametech.yangjian.agent.plugin.redisson.context;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.bean.MetricData;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MultiReportFactory;
import com.yametech.yangjian.agent.api.interceptor.IMethodAOP;
import com.yametech.yangjian.agent.plugin.redisson.util.ClassUtil;
import org.redisson.config.Config;
import org.redisson.connection.ConnectionManager;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dengliming
 * @date 2020/5/7
 */
public class ConnectionManagerInterceptor implements IMethodAOP {

    private static IReportData report = MultiReportFactory.getReport("collect");

    @Override
    public BeforeResult before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        return null;
    }

    @Override
    public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult beforeResult, Object ret,
                        Throwable t, Map globalVar) throws Throwable {
        ConnectionManager connectionManager = (ConnectionManager) thisObj;
        Config config = connectionManager.getCfg();

        // 这里通过反射调用获取主要因为Config类不提供get方法获取，目前只有RedissonClient初始化时候才会调用所以频率很低可暂不考虑性能影响
        String redisUrl = null;
        Object serversConfig = ClassUtil.getObjectField(config, "sentinelServersConfig");
        if (serversConfig != null) {
            redisUrl = buildRedisUrl((Collection) ClassUtil.getObjectField(serversConfig, "sentinelAddresses"));
            ((IContext) thisObj)._setAgentContext(ContextConstants.REDIS_URL_CONTEXT_KEY, redisUrl);
            reportDependency(redisUrl);
            return ret;
        }

        serversConfig = ClassUtil.getObjectField(config, "masterSlaveServersConfig");
        if (serversConfig != null) {
            Object masterAddress = ClassUtil.getObjectField(serversConfig, "masterAddress");
            redisUrl = getRealUrl(masterAddress) + "," + buildRedisUrl((Collection) ClassUtil.getObjectField(serversConfig, "slaveAddresses"));
            ((IContext) thisObj)._setAgentContext(ContextConstants.REDIS_URL_CONTEXT_KEY, redisUrl);
            reportDependency(redisUrl);
            return ret;
        }

        serversConfig = ClassUtil.getObjectField(config, "clusterServersConfig");
        if (serversConfig != null) {
            redisUrl = buildRedisUrl((Collection) ClassUtil.getObjectField(serversConfig, "nodeAddresses"));
            ((IContext) thisObj)._setAgentContext(ContextConstants.REDIS_URL_CONTEXT_KEY, redisUrl);
            reportDependency(redisUrl);
            return ret;
        }

        serversConfig = ClassUtil.getObjectField(config, "replicatedServersConfig");
        if (serversConfig != null) {
            redisUrl = buildRedisUrl((Collection) ClassUtil.getObjectField(serversConfig, "nodeAddresses"));
            ((IContext) thisObj)._setAgentContext(ContextConstants.REDIS_URL_CONTEXT_KEY, redisUrl);
            reportDependency(redisUrl);
        }
        return ret;
    }

    private String buildRedisUrl(Collection nodeAddresses) {
        StringBuilder sb = new StringBuilder();
        if (nodeAddresses != null && !nodeAddresses.isEmpty()) {
            for (Object uri : nodeAddresses) {
                sb.append(getRealUrl(uri)).append(",");
            }
        }
        return sb.toString();
    }

    private String getRealUrl(Object obj) {
        if (obj instanceof String) {
            return ((String) obj).replace("redis://", "");
        } else if (obj instanceof URI) {
            URI uri = (URI) obj;
            return uri.getHost() + ":" + uri.getPort();
        }
        return null;
    }

    private void reportDependency(String url) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.Tags.PEER, url);
        report.report(MetricData.get(null, Constants.DEPENDENCY_PATH + Constants.Component.REDISSON, params));
    }
}
