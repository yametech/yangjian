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

package com.yametech.yangjian.agent.tests.tool.bean;

/**
 * @author dengliming
 */
public class EventMetric {

    // 类型，如：dubbo-client、dubbo-server、spring-controller、redis、mysql、kafka、rabbitmq、mongo、okhttp、apache-httpclient
    private String type;
    private long eventTime;// 事件发生的时间毫秒数
    // 标识，包含：
    // 方法的耗时为方法定义、kafka/rabbitmq耗时则为topic、redis耗时为key、mysql耗时为表名>操作类型、
    // mongo耗时为集合名>操作、http耗时为域名（不能带url，url中可能包含动态字段，导致统计量很大）、
    // 其他自定义耗时（redis自定义匹配规则，将满足规则的放入一个分组、http请求自定义规则、mysql sql自定义匹配规则）
    private String sign;
    private long useTime;
    private long num;
    private long errorTotal;
    private long rtTotal;
    private String serviceName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public long getErrorTotal() {
        return errorTotal;
    }

    public void setErrorTotal(long errorTotal) {
        this.errorTotal = errorTotal;
    }

    public long getRtTotal() {
        return rtTotal;
    }

    public void setRtTotal(long rtTotal) {
        this.rtTotal = rtTotal;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
