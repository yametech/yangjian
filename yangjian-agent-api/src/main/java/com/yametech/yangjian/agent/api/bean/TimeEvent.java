/**
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

package com.yametech.yangjian.agent.api.bean;

import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.convert.statistic.StatisticType;

public class TimeEvent {
    private StatisticType[] statisticTypes;// 使用的统计类型
    private String type;// 类型，如：dubbo-client、dubbo-server、spring-controller、redis、mysql、kafka、rabbitmq、mongo、okhttp、apache-httpclient
    private long eventTime;// 事件发生的时间毫秒数
    // 标识，包含：
    // 方法的耗时为方法定义、kafka/rabbitmq耗时则为topic、redis耗时为key、mysql耗时为表名>操作类型、
    // mongo耗时为集合名>操作、http耗时为域名（不能带url，url中可能包含动态字段，导致统计量很大）、
    // 其他自定义耗时（redis自定义匹配规则，将满足规则的放入一个分组、http请求自定义规则、mysql sql自定义匹配规则）
    private String identify;
    private long useTime;// 耗时
    private long number = 1;// 数量，默认为1，批量操作时该值为批量数量
    public static final StatisticType[] DEFAULT_STATISTIC_TYPES = new StatisticType[]{StatisticType.RT};

    public TimeEvent() {
        this(DEFAULT_STATISTIC_TYPES);
    }

    // TODO 去掉
    public TimeEvent(StatisticType[] statisticTypes) {
        if (statisticTypes == null || statisticTypes.length == 0) {
            throw new IllegalArgumentException("必须指定至少一个StatisticType");
        }
        this.statisticTypes = statisticTypes;
    }

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

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public StatisticType[] getStatisticTypes() {
        return statisticTypes;
    }
    
    public void setStatisticTypes(StatisticType[] statisticTypes) {
		this.statisticTypes = statisticTypes;
	}

    @Override
    public String toString() {
    	return StringUtil.join(new Object[] {StringUtil.join(statisticTypes), type, eventTime, identify, useTime});
    }

}
