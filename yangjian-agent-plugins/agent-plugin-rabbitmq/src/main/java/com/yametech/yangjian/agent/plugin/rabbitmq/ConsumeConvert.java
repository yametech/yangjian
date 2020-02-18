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

package com.yametech.yangjian.agent.plugin.rabbitmq;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.convert.IMethodConvert;
import com.yametech.yangjian.agent.plugin.rabbitmq.bean.MqInfo;
import com.yametech.yangjian.agent.plugin.rabbitmq.context.ContextConstants;
import com.rabbitmq.client.Envelope;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 输出kafka Qps数量
 *
 * @author liuzhao
 * @Description
 * @date 2019年11月6日 下午8:07:04
 */
public class ConsumeConvert implements IMethodConvert {
	
	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        if (!(thisObj instanceof IContext)) {
            return null;
        }
        MqInfo mqInfo = (MqInfo) ((IContext) thisObj)._getAgentContext(ContextConstants.RABBITMQ_CONTEXT_KEY);
        if (mqInfo == null) {
            return null;
        }

        if (StringUtil.isEmpty(mqInfo.getTopic())) {
            mqInfo.setTopic(getExchange(allArguments[1]));
        }

        TimeEvent event = get(startTime);
		event.setIdentify(mqInfo.getTopic() + Constants.IDENTIFY_SEPARATOR + mqInfo.getConsumeGroup());
		return Arrays.asList(event);
    }

    private String getExchange(Object object) {
	    if (object instanceof Envelope) {
	        return ((Envelope) object).getExchange();
        }
	    return null;
    }
}
