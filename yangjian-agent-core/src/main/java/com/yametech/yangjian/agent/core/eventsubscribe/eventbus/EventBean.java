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

package com.yametech.yangjian.agent.core.eventsubscribe.eventbus;

import java.lang.reflect.Method;
import java.util.Map;

import com.yametech.yangjian.agent.core.eventsubscribe.base.EventSubscribe;

public class EventBean {
	private EventSubscribe eventSubscribe;
	private Object sourceObj;
	private Object[] allArguments;
	private Map<String, Object> extraParams;
	private Method method;
	private Object returnData;
	private Throwable exception;
	
	public void reset(EventSubscribe eventSubscribe, Object sourceObj, Object[] allArguments, Map<String, Object> extraParams,
			Method method, Object returnData, Throwable exception) {
		this.eventSubscribe = eventSubscribe;
		this.sourceObj = sourceObj;
		this.allArguments = allArguments;
		this.extraParams = extraParams;
		this.method = method;
		this.returnData = returnData;
		this.exception = exception;
	}
	
	public void call() {
		eventSubscribe.notify(sourceObj, allArguments, extraParams, method, returnData, exception);
	}

	public EventSubscribe getEventSubscribe() {
		return eventSubscribe;
	}

	public Object getSourceObj() {
		return sourceObj;
	}

	public Object[] getAllArguments() {
		return allArguments;
	}

	public Method getMethod() {
		return method;
	}

	public Object getReturnData() {
		return returnData;
	}
	
	public Throwable getException() {
		return exception;
	}
	
}
