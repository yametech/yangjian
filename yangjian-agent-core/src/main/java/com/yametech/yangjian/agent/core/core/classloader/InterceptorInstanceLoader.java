/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package com.yametech.yangjian.agent.core.core.classloader;

import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.util.Value;

import java.util.concurrent.ConcurrentHashMap;

/**
 * The <code>InterceptorInstanceLoader</code> is a classes finder and container.
 * <p>
 * This is a very important class in sky-walking's auto-instrumentation mechanism. If you want to fully understand why
 * need this, and how it works, you need have knowledge about Classloader appointment mechanism.
 * <p>
 * Created by wusheng on 16/8/2.
 */
public class InterceptorInstanceLoader {
	private static ILogger log = LoggerFactory.getLogger(InterceptorInstanceLoader.class);
    private static ConcurrentHashMap<String, Object> INSTANCE_CACHE = new ConcurrentHashMap<>();

    /**
     * Load an instance of interceptor, and keep it singleton.
     * Create {@link AgentClassLoader} for each targetClassLoader, as an extend classloader.
     * It can load interceptor classes from plugins, activations folders.
     *
     * @param className         the interceptor class, which is expected to be found
     * @param targetClassLoader the class loader for current application context
     * @param <T>               expected type
     * @return the type reference.
     * @throws Exception
     */
    public static <T> T load(String classKey, String className, ClassLoader targetClassLoader) throws Throwable {
        if (targetClassLoader == null) {
            targetClassLoader = InterceptorInstanceLoader.class.getClassLoader();
        }
        String instanceKey = classKey + "_OF_" + targetClassLoader.getClass().getName() + "@" + Integer.toHexString(targetClassLoader.hashCode());
        ClassLoader finalTargetClassLoader = targetClassLoader;
        Value<Throwable> exception = Value.absent();
        Object inst = INSTANCE_CACHE.computeIfAbsent(instanceKey, key -> {
            ClassLoader pluginLoader = AgentClassLoader.getCacheClassLoader(finalTargetClassLoader);
            try {
                Object instance = Class.forName(className, true, pluginLoader).newInstance();
                log.info("InterceptorInstanceLoader:{}	{}	{}  {}", className, pluginLoader, instanceKey, instance.getClass().getClassLoader());
                return instance;
            } catch (Throwable e) {
                exception.set(e);
                log.warn("InterceptorInstanceLoader exception:{}	{}	{}", className, pluginLoader, instanceKey);
                return null;
            }
        });
        if(exception.get() != null) {
            throw exception.get();
        }
        //noinspection unchecked
        return (T) inst;
    }
}
