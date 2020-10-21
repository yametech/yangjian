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
package com.yametech.yangjian.agent.core.core.interceptor;

/**
 * @author dengliming
 * @date 2020/3/23
 */
public class InterceptorWrapper<T> {
    /**
     * 实际拦截器
     */
    private T interceptor;
    /**
     * 用于控制是否开启
     */
    private boolean enable = true;

    public InterceptorWrapper(T interceptor) {
        this.interceptor = interceptor;
    }

    public T getInterceptor() {
        return interceptor;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
