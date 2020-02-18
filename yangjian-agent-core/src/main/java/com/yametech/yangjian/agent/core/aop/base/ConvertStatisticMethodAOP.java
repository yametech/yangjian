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

package com.yametech.yangjian.agent.core.aop.base;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.convert.IAsyncConvert;
import com.yametech.yangjian.agent.api.convert.IStatisticMethodAsyncConvert;
import com.yametech.yangjian.agent.api.convert.IStatisticMethodBeforeAsyncConvert;
import com.yametech.yangjian.agent.api.convert.IStatisticMethodBeforeConvert;
import com.yametech.yangjian.agent.api.convert.IStatisticMethodConvert;
import com.yametech.yangjian.agent.api.convert.statistic.StatisticType;
import com.yametech.yangjian.agent.api.interceptor.IStaticMethodAOP;
import com.yametech.yangjian.agent.core.aop.BaseConvertAOP;

/**
 * 注意：该类不要修改路径及类名，其他地方有类字符串引用
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年12月24日 下午9:51:47
 */
public class ConvertStatisticMethodAOP extends BaseConvertAOP implements IStaticMethodAOP<Long> {
	
	@Override
	public BeforeResult<Long> before(Object[] allArguments, Method method) throws Throwable {
		if(convert instanceof IStatisticMethodBeforeAsyncConvert) {
			IStatisticMethodBeforeAsyncConvert thisConvert = (IStatisticMethodBeforeAsyncConvert)convert;
        	List<Object> datas = thisConvert.convert(allArguments, method);
        	if(datas != null) {
        		for(Object data : datas) {
        			// TODO 此处增加批量发布，并且限制每次批量的条数，超过后分批发布
        			metricEventBus.publish(event -> {// 共用对象实例，仅变更字段值，必须包含所有字段设值，否则会包含之前的值（实例共用）
        				init(event, thisConvert, data, null, 0, null, 0, 0);
        			});
        		}
        	}
		} else if(convert instanceof IStatisticMethodBeforeConvert) {
			IStatisticMethodBeforeConvert thisConvert = (IStatisticMethodBeforeConvert)convert;
        	List<TimeEvent> datas = thisConvert.convert(allArguments, method);
        	if(datas != null) {
        		for(TimeEvent timeEvent : datas) {
        			// TODO 此处增加批量发布，并且限制每次批量的条数，超过后分批发布
        			metricEventBus.publish(event -> {// 共用对象实例，仅变更字段值，必须包含所有字段设值，否则会包含之前的值（实例共用）
        				init(event, null, null, timeEvent.getStatisticTypes(), timeEvent.getEventTime(), 
        						timeEvent.getIdentify(), timeEvent.getUseTime(), timeEvent.getNumber());
        			});
        		}
        	}
		}
        return new BeforeResult<>(null, System.currentTimeMillis(), null);
	}

	@Override
	public Object after(Object[] allArguments, Method method, BeforeResult<Long> beforeResult,
			Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
		long startTime = beforeResult.getLocalVar();
        if(convert instanceof IStatisticMethodAsyncConvert) {
        	IStatisticMethodAsyncConvert thisConvert = (IStatisticMethodAsyncConvert)convert;
        	List<Object> datas = thisConvert.convert(startTime, allArguments, method, ret, t, globalVar);
        	if(datas != null) {
        		for(Object data : datas) {
        			// TODO 此处增加批量发布，并且限制每次批量的条数，超过后分批发布
        			metricEventBus.publish(event -> {// 共用对象实例，仅变更字段值，必须包含所有字段设值，否则会包含之前的值（实例共用）
        				init(event, thisConvert, data, null, 0, null, 0, 0);
        			});
        		}
        	}
        } else if(convert instanceof IStatisticMethodConvert) {
        	IStatisticMethodConvert thisConvert = (IStatisticMethodConvert)convert;
        	List<TimeEvent> datas = thisConvert.convert(startTime, allArguments, method, ret, t, globalVar);
        	if(datas != null) {
        		for(TimeEvent timeEvent : datas) {
        			// TODO 此处增加批量发布，并且限制每次批量的条数，超过后分批发布
        			metricEventBus.publish(event -> {// 共用对象实例，仅变更字段值，必须包含所有字段设值，否则会包含之前的值（实例共用）
        				init(event, null, null, timeEvent.getStatisticTypes(), timeEvent.getEventTime(), 
        						timeEvent.getIdentify(), timeEvent.getUseTime(), timeEvent.getNumber());
        			});
        		}
        	}
        }
        return ret;
	}
	
	private void init(ConvertTimeEvent event, IAsyncConvert convert, Object data, 
			StatisticType[] statisticTypes, long eventTime, String identify, long useTime, long number) {
		event.setConvert(convert);
        event.setData(data);
        
        event.setStatisticTypes(statisticTypes);
        event.setType(metricMatcher.type());
        event.setEventTime(eventTime);
        event.setIdentify(identify);
        event.setUseTime(useTime);
        event.setNumber(number);
	}

}
