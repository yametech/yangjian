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
package com.yametech.yangjian.agent.server.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author dengliming
 * @date 2020/3/14
 */
@Data
@Accessors(chain = true)
public class MetricsParameter {
    /**
     * 统计时间戳（单位：毫秒）
     */
    private long timestamp;
    /**
     * 应用实例（HOST:PORT）
     */
    private String instance;
    /**
     * 应用名（英文小写）
     */
    private String serviceName;
    /**
     * 指标数据类型 如：resources|statistic/hikaricp/connectionPool
     */
    private String dataType;
    /**
     * 完整指标参数
     */
    private Map<String, Object> params;

    public static final String PARAM_SECOND_KEY = "second";
    public static final String PARAM_IP_KEY = "ip";
    public static final String PARAM_SERVICE_NAME_KEY = "serviceName";
    public static final String PARAM_DATA_TYPE_KEY = "dataType";
}
