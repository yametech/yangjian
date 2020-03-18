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
package com.yametech.yangjian.agent.exporter.resolver;

/**
 * 解析指标接口
 *
 * @author dengliming
 * @date 2020/3/10
 */
public interface IMetricsResolver<K, V> {

    /**
     * 用于判断是否支持
     *
     * @param parameter
     * @return
     */
    default boolean supports(K parameter) {
        return false;
    }

    /**
     * 解析方法
     *
     * @param parameter
     * @return
     */
    V resolve(K parameter);
}
