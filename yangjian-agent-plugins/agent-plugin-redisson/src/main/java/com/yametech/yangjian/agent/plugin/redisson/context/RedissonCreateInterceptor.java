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

import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.interceptor.IStaticMethodAOP;
import com.yametech.yangjian.agent.plugin.redisson.util.ClassUtil;
import org.redisson.config.Config;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import static com.yametech.yangjian.agent.plugin.redisson.util.RedisUtil.*;

/**
 *
 * @author dengliming
 * @date 2020/7/27
 */
public class RedissonCreateInterceptor implements IStaticMethodAOP {

    @Override
    public BeforeResult before(Object[] allArguments, Method method) throws Throwable {
        return null;
    }

    @Override
    public Object after(Object[] allArguments, Method method, BeforeResult beforeResult, Object ret, Throwable t, Map globalVar) throws Throwable {
        Config config = (Config) allArguments[0];
        // 这里通过反射调用获取主要因为Config类不提供get方法获取，目前只有RedissonClient初始化时候才会调用所以频率很低可暂不考虑性能影响
        String redisUrl = null;
        Object serversConfig = ClassUtil.getObjectField(config, "sentinelServersConfig");
        if (serversConfig != null) {
            redisUrl = buildRedisUrl((Collection) ClassUtil.getObjectField(serversConfig, "sentinelAddresses"));
            reportDependency(redisUrl, Constants.DbMode.SENTINEL);
            return ret;
        }

        serversConfig = ClassUtil.getObjectField(config, "masterSlaveServersConfig");
        if (serversConfig != null) {
            Object masterAddress = ClassUtil.getObjectField(serversConfig, "masterAddress");
            redisUrl = getRealUrl(masterAddress) + "," + buildRedisUrl((Collection) ClassUtil.getObjectField(serversConfig, "slaveAddresses"));
            reportDependency(redisUrl, Constants.DbMode.MASTER_SLAVE);
            return ret;
        }

        serversConfig = ClassUtil.getObjectField(config, "clusterServersConfig");
        if (serversConfig != null) {
            redisUrl = buildRedisUrl((Collection) ClassUtil.getObjectField(serversConfig, "nodeAddresses"));
            reportDependency(redisUrl, Constants.DbMode.CLUSTER);
            return ret;
        }

        serversConfig = ClassUtil.getObjectField(config, "replicatedServersConfig");
        if (serversConfig != null) {
            redisUrl = buildRedisUrl((Collection) ClassUtil.getObjectField(serversConfig, "nodeAddresses"));
            reportDependency(redisUrl, Constants.DbMode.REPLICATED);
            return ret;
        }

        serversConfig = ClassUtil.getObjectField(config, "singleServerConfig");
        if (serversConfig != null) {
            redisUrl = getRealUrl(ClassUtil.getObjectField(serversConfig, "address"));
            reportDependency(redisUrl, Constants.DbMode.SINGLE);
        }
        return ret;
    }
}
