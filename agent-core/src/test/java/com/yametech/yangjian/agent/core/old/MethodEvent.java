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
package com.yametech.yangjian.agent.core.old;

import java.lang.reflect.Method;
import java.util.Map;

public class MethodEvent {
//	private boolean before;// 是否为调用方法前事件
	private Object instance;// 类实例，静态类则为null
	private Method method;// 调用的方法
	private Object[] arguments;// 方法参数
	private Object ret;// 方法返回值
	private long eventTime;// 发生时间毫秒数，可能值：方法调用前的时间、方法调用后的时间
	private long startTime;// 方法调用前的时间毫秒数，如果是方法调用前执行事件，该值与eventTime一样
	private Throwable throwable;// 方法调用的异常信息，无异常时该值为null
	private Map<Class<?>, Object> globalVar;// IMethodAOP before中生成的globalVar，key为IMethodAOP实例class

//	public boolean isBefore() {
//		return before;
//	}
//
//	public void setBefore(boolean before) {
//		this.before = before;
//	}
	
	public Object getInstance() {
		return instance;
	}
	
	public void setInstance(Object instance) {
		this.instance = instance;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public Object getRet() {
		return ret;
	}

	public void setRet(Object ret) {
		this.ret = ret;
	}

	public long getEventTime() {
		return eventTime;
	}

	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	
	public Map<Class<?>, Object> getGlobalVar() {
		return globalVar;
	}
	
	public void setGlobalVar(Map<Class<?>, Object> globalVar) {
		this.globalVar = globalVar;
	}

}
