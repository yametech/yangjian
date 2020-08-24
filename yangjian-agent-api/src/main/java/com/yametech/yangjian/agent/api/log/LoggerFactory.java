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
package com.yametech.yangjian.agent.api.log;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public final class LoggerFactory {

    static volatile ILoggerServiceProvider PROVIDER;

    public static ILogger getLogger(Class<?> clazz) {
        return getLogger(clazz.getCanonicalName());
    }

    public static ILogger getLogger(String name) {
        return getILoggerFactory().getLogger(name);
    }

    private static ILoggerFactory getILoggerFactory() {
        return getProvider().getLoggerFactory();
    }

    /**
     * 获取自定义的LoggerFactory提供者
     * <p>
     * 注：
     * 1、需实现com.yametech.yangjian.agent.api.log.ILoggerServiceProvider
     * 2、创建META-INF/services/com.yametech.yangjian.agent.api.log.ILoggerServiceProvider
     *
     * @return  xx
     */
    private static ILoggerServiceProvider getProvider() {
        if (PROVIDER != null) {
            return PROVIDER;
        }
        synchronized (LoggerFactory.class) {
            if (PROVIDER == null) {
                try {
                    ServiceLoader<ILoggerServiceProvider> serviceLoader = ServiceLoader.load(ILoggerServiceProvider.class);
                    List<ILoggerServiceProvider> providerList = new ArrayList<>();
                    for (ILoggerServiceProvider provider : serviceLoader) {
                        providerList.add(provider);
                    }
                    PROVIDER = providerList != null && !providerList.isEmpty() ? providerList.get(0) : new NOPLoggerProvider();
                } catch (Exception e) {
                    PROVIDER = new NOPLoggerProvider();
                    printLog("Found logger provider error.", e);
                }
                PROVIDER.initialize();
                printLog(String.format("Found logger provider [%s]", PROVIDER.getClass()));
            }
        }
        return PROVIDER;
    }

    private static void printLog(String msg) {
        printLog(msg, null);
    }

    private static void printLog(String msg, Throwable t) {
        System.err.println("AgentLog: " + msg);
        if (t != null) {
            System.err.println("Occur exception:");
            t.printStackTrace();
        }
    }
}
