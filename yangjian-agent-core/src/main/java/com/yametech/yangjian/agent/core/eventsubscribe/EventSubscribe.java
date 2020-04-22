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
package com.yametech.yangjian.agent.core.eventsubscribe;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.util.RateLimit;


public class EventSubscribe {
	private static final ILogger LOG = LoggerFactory.getLogger(EventSubscribe.class);
	private static final int REGIST_MAX_NUM = 100;// 最大注册个数(subscribes的最大长度)
	private static final RateLimit LIMITER = RateLimit.create(10);
	private Map<Method, Object> subscribes = new ConcurrentHashMap<>();
	private String className;
	private String methodName;
	private String[] params;
	
	public EventSubscribe(String className, String methodName, String[] params) {
		this.className = className;
		this.methodName = methodName;
		this.params = params;
	}
	
	/**
	 * 通知注册方
	 * 注意 : 该方法名不允许改动，EventDispatcher类中有检测使用
	 * @param sourceMethod
	 * @param args
	 */
	public void notify(Object sourceObj, Object[] allArguments, Method method, Object ret, Throwable t) {
		if(subscribes.size() == 0) {
			return;
		}
		subscribes.forEach((subscribeMethod, instance) -> {
			try {
				subscribeMethod.invoke(instance, getArguments(sourceObj, subscribeMethod, allArguments, ret, t));
			} catch (Exception e) {
				if(LIMITER.tryAcquire()) {
					LOG.warn(e, "事件订阅消费异常:{} {}", method, subscribeMethod);
				}
			}
		});
	}
	
	/**
	 * 获取订阅方法的参数
	 * @param method
	 * @param arguments
	 * @param ret
	 * @param t
	 * @return
	 */
	private Object[] getArguments(Object sourceObj, Method method, Object[] arguments, Object ret, Throwable t) {
		int argLength = arguments == null ? 0 : arguments.length;
		if(method.getParameterCount() == argLength) {
			return arguments;
//		} else if(method.getParameterCount() < argLength) {// 理论上不会发生
//			return null;
		}
		Object[] newArguments = new Object[method.getParameterCount()];
		if(argLength > 0) {
			System.arraycopy(arguments, 0, newArguments, 0, argLength);
		}
		for(int i = argLength; i < method.getParameterCount(); i++) {
			if(Throwable.class.isAssignableFrom(method.getParameterTypes()[i])) {
				newArguments[i] = t;
			} else if(sourceObj.getClass().isAssignableFrom(method.getParameterTypes()[i])) {
				newArguments[i] = sourceObj;
			} else if(ret.getClass().isAssignableFrom(method.getParameterTypes()[i])) {
				newArguments[i] = ret;
			} else {
				newArguments[i] = null;
			}
		}
		return newArguments;
	}
	
	/**
	 * 注册事件监听(按照method去重)，并检测：参数是否匹配、监听方与事件方是否一致；
	 * @param method
	 * @param instance
	 * @return
	 */
	public boolean regist(Method method, Object instance) {
		if(subscribes.containsKey(method)) {
			return true;
		}
		if(method.getParameterTypes().length < params.length) {
			LOG.info("参数个数不匹配{} - {}", method.getParameterTypes().length, params.length);
			return false;
		}
		for(int i = 0; i < params.length; i++) {
			if(!params[i].equals(method.getParameterTypes()[i].getName())) {
				LOG.info("第{}个参数类型不匹配应该为{}，实际为{}", i, params[i], method.getParameterTypes()[i].getName());
				return false;
			}
		}
		if(method.getDeclaringClass().getTypeName().equals(className) && method.getName().equals(methodName)) {
			LOG.info("注册方法与事件源方法不能一样：{}", method);
			return false;
		}
		if(subscribes.size() > REGIST_MAX_NUM) {
			LOG.info("注册个数超出最大值{}，无法注册", REGIST_MAX_NUM);
			return false;
		}
		subscribes.put(method, instance);
		return true;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public String[] getParams() {
		return params;
	}
	
	public Map<Method, Object> getSubscribes() {
		return subscribes;
	}
}
