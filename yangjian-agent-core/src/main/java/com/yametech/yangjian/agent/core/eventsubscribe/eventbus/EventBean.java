package com.yametech.yangjian.agent.core.eventsubscribe.eventbus;

import java.lang.reflect.Method;

import com.yametech.yangjian.agent.core.eventsubscribe.EventSubscribe;

public class EventBean {
	private EventSubscribe eventSubscribe;
	private Object sourceObj;
	private Object[] allArguments;
	private Method method;
	private Object returnData;
	private Throwable exception;
	
	public void reset(EventSubscribe eventSubscribe, Object sourceObj, Object[] allArguments, 
			Method method, Object returnData, Throwable exception) {
		this.eventSubscribe = eventSubscribe;
		this.sourceObj = sourceObj;
		this.allArguments = allArguments;
		this.method = method;
		this.returnData = returnData;
		this.exception = exception;
	}
	
	public void call() {
		eventSubscribe.notify(sourceObj, allArguments, method, returnData, exception);
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
