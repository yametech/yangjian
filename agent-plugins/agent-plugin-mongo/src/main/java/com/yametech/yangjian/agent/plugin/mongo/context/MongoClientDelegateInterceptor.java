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
package com.yametech.yangjian.agent.plugin.mongo.context;

import com.mongodb.ServerAddress;
import com.mongodb.internal.connection.Cluster;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;

/**
 * 拦截构造方法获取mongo连接地址
 */
public class MongoClientDelegateInterceptor implements IConstructorListener {

    @Override
    public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
        Cluster cluster = (Cluster) allArguments[0];
        ((IContext) thisObj)._setAgentContext(ContextConstants.MONGO_SERVER_URL, getServerUrl(cluster));
    }

    private String getServerUrl(Cluster cluster) {
        StringBuilder sb = new StringBuilder();
        for (ServerAddress address : cluster.getSettings().getHosts()) {
            sb.append(address.getHost()).append(":").append(address.getPort()).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }
}
