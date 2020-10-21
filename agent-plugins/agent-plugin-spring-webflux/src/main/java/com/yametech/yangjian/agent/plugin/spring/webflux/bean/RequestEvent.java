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
package com.yametech.yangjian.agent.plugin.spring.webflux.bean;

/**
 * 用于封装一次请求事件
 *
 * @author dengliming
 * @date 2020/3/18
 */
public class RequestEvent {
    /**
     * 请求的方法名（对应spring controller的方法 如：com.example.webflux.controller.TestController.home()）
     */
    private String methodName;
    /**
     * 对应请求的开始时间（为了计算RT）
     */
    private long startTime;

    public String getMethodName() {
        return methodName;
    }

    public RequestEvent setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public long getStartTime() {
        return startTime;
    }

    public RequestEvent setStartTime(long startTime) {
        this.startTime = startTime;
        return this;
    }
}
