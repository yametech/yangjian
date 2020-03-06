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

package com.yametech.yangjian.agent.core.metric.base;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.convert.IAsyncConvert;
import com.yametech.yangjian.agent.api.convert.IMethodAsyncConvert;
import com.yametech.yangjian.agent.api.convert.IMethodBeforeAsyncConvert;
import com.yametech.yangjian.agent.api.convert.IMethodBeforeConvert;
import com.yametech.yangjian.agent.api.convert.IMethodConvert;
import com.yametech.yangjian.agent.api.convert.statistic.StatisticType;
import com.yametech.yangjian.agent.api.interceptor.IMethodAOP;
import com.yametech.yangjian.agent.core.metric.BaseConvertAOP;

/**
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年12月24日 下午9:50:56
 */
public class ConvertMethodAOP extends BaseConvertAOP implements IMethodAOP<Long> {
	
	@Override
	public BeforeResult<Long> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
		if(convert instanceof IMethodBeforeAsyncConvert) {
			IMethodBeforeAsyncConvert thisConvert = (IMethodBeforeAsyncConvert)convert;
        	List<Object> datas = thisConvert.convert(thisObj, allArguments, method);
        	if(datas != null) {
        		for(Object data : datas) {
        			// TODO 此处增加批量发布，并且限制每次批量的条数，超过后分批发布
        			metricEventBus.publish(event -> {// 共用对象实例，仅变更字段值，必须包含所有字段设值，否则会包含之前的值（实例共用）
        				initEvent(event, thisConvert, data, null, 0, null, 0, 0);
        			});
        		}
        	}
		} else if(convert instanceof IMethodBeforeConvert) {
			IMethodBeforeConvert thisConvert = (IMethodBeforeConvert)convert;
        	List<TimeEvent> datas = thisConvert.convert(thisObj, allArguments, method);
        	if(datas != null) {
        		for(TimeEvent timeEvent : datas) {
        			// TODO 此处增加批量发布，并且限制每次批量的条数，超过后分批发布
        			metricEventBus.publish(event -> {// 共用对象实例，仅变更字段值，必须包含所有字段设值，否则会包含之前的值（实例共用）
        				initEvent(event, null, null, timeEvent.getStatisticTypes(), timeEvent.getEventTime(), 
        						timeEvent.getIdentify(), timeEvent.getUseTime(), timeEvent.getNumber());
        			});
        		}
        	}
		}
        return new BeforeResult<>(null, System.currentTimeMillis(), null);
	}

	@Override
	public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult<Long> beforeResult,
			Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
		long startTime = beforeResult.getLocalVar();
        if(convert instanceof IMethodAsyncConvert) {
        	IMethodAsyncConvert thisConvert = (IMethodAsyncConvert)convert;
        	List<Object> datas = thisConvert.convert(thisObj, startTime, allArguments, method, ret, t, globalVar);
        	if(datas != null) {
        		for(Object data : datas) {
        			// TODO 此处增加批量发布，并且限制每次批量的条数，超过后分批发布
        			metricEventBus.publish(event -> {// 共用对象实例，仅变更字段值，必须包含所有字段设值，否则会包含之前的值（实例共用）
        				initEvent(event, thisConvert, data, null, 0, null, 0, 0);
        			});
        		}
        	}
        } else if(convert instanceof IMethodConvert) {
        	IMethodConvert thisConvert = (IMethodConvert)convert;
        	List<TimeEvent> datas = thisConvert.convert(thisObj, startTime, allArguments, method, ret, t, globalVar);
        	if(datas != null) {
        		for(TimeEvent timeEvent : datas) {
        			// TODO 此处增加批量发布，并且限制每次批量的条数，超过后分批发布
        			metricEventBus.publish(event -> {// 共用对象实例，仅变更字段值，必须包含所有字段设值，否则会包含之前的值（实例共用）
        				initEvent(event, null, null, timeEvent.getStatisticTypes(), timeEvent.getEventTime(), 
        						timeEvent.getIdentify(), timeEvent.getUseTime(), timeEvent.getNumber());
        			});
        		}
        	}
        }
        return ret;
	}
	
	private void initEvent(ConvertTimeEvent event, IAsyncConvert convert, Object data, 
			StatisticType[] statisticTypes, long eventTime, String identify, long useTime, long number) {
		event.setConvert(convert);
        event.setData(data);
        
        event.setStatisticTypes(statisticTypes);
        event.setType(type);
        event.setEventTime(eventTime);
        event.setIdentify(identify);
        event.setUseTime(useTime);
        event.setNumber(number);
	}

}
