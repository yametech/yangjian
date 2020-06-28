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

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;

import java.util.List;

/**
 * 拦截方法传递服务地址
 *
 * @author dengliming
 * @date 2020/6/23
 */
public class MongoClientImplInterceptor implements IConstructorListener {

    @Override
    public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
        if (!(allArguments[0] instanceof MongoClientSettings)) {
            return;
        }

        final MongoClientSettings mongoClientSettings = (MongoClientSettings) allArguments[0];
        List<ServerAddress> lists = mongoClientSettings.getClusterSettings().getHosts();
        if (lists == null || lists.size() == 0) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (ServerAddress serverAddress : lists) {
            sb.append(serverAddress.getHost()).append(":").append(serverAddress.getPort()).append(",");
        }
        String serverUrl = sb.substring(0, sb.length() - 1);
        ((IContext) thisObj)._setAgentContext(ContextConstants.MONGO_SERVER_URL, serverUrl);
    }
}
