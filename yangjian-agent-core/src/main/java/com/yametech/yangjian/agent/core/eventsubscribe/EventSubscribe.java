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
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.util.RateLimit;


public class EventSubscribe {
	private static final ILogger LOG = LoggerFactory.getLogger(EventSubscribe.class);
	private static final int REGIST_MAX_NUM = 100;// 最大注册个数(subscribes的最大长度)
	private static final RateLimit LIMITER = RateLimit.create(10);
	private Map<Method, Entry<Object, Object[]>> subscribes = new ConcurrentHashMap<>();
	private boolean ignoreParams;
	private String className;
	private String methodName;
	private String[] params;
	
	public EventSubscribe(boolean ignoreParams, String className, String methodName, String[] params) {
		this.ignoreParams = ignoreParams;
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
				subscribeMethod.invoke(instance.getKey(), getArguments(Arrays.copyOf(instance.getValue(), instance.getValue().length), sourceObj, subscribeMethod, allArguments, ret, t));
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
	private Object[] getArguments(Object[] defaultArguments, Object sourceObj, Method method, Object[] arguments, Object ret, Throwable t) {
		if(ignoreParams) {
			setExtraParams(0, method, defaultArguments, sourceObj, ret, t);
			return defaultArguments;
		}
		int argLength = arguments == null ? 0 : arguments.length;
		if(method.getParameterCount() == argLength) {
			return arguments;
//		} else if(method.getParameterCount() < argLength) {// 注册时有检查不会发生
//			return null;
		}
		if(argLength > 0) {
			System.arraycopy(arguments, 0, defaultArguments, 0, argLength);
		}
		setExtraParams(argLength, method, defaultArguments, sourceObj, ret, t);
		return defaultArguments;
	}
	
	private void setExtraParams(int startIndex, Method method, Object[] newArguments, Object sourceObj, Object ret, Throwable t) {
		boolean sourceObjInit = false;
		boolean throwableInit = false;
		boolean returnInit = false;
		for(int i = startIndex; i < method.getParameterCount(); i++) {
			if(t != null && Throwable.class.isAssignableFrom(method.getParameterTypes()[i]) && !throwableInit) {
				newArguments[i] = t;
				throwableInit = true;
			} else if(sourceObj.getClass().isAssignableFrom(method.getParameterTypes()[i]) && !sourceObjInit) {
				newArguments[i] = sourceObj;
				sourceObjInit = true;
			} else if(ret != null && ret.getClass().isAssignableFrom(method.getParameterTypes()[i]) && !returnInit) {
				newArguments[i] = ret;
				returnInit = true;
			}
		}
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
		if(!ignoreParams && method.getParameterTypes().length < params.length) {
			LOG.info("参数个数不匹配{} - {}", method.getParameterTypes().length, params.length);
			return false;
		}
		if(!ignoreParams) {
			for(int i = 0; i < params.length; i++) {
				if(!params[i].equals(method.getParameterTypes()[i].getName())) {
					LOG.info("第{}个参数类型不匹配应该为{}，实际为{}", i + 1, params[i], method.getParameterTypes()[i].getName());
					return false;
				}
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
		subscribes.put(method, new SimpleEntry<>(instance, getDefaultArguments(method)));
		return true;
	}
	
	/**
	 * 获取默认的参数，用于预设基本数据类型初始值
	 * @param method
	 * @return
	 */
	private Object[] getDefaultArguments(Method method) {
		Object[] arguments = new Object[method.getParameterCount()];
		for(int i = 0; i < method.getParameterCount(); i++) {
			String parameterClassName = method.getParameterTypes()[i].getName();
			if("byte".equals(parameterClassName)) {
				arguments[i] = (byte)0;
			} else if("short".equals(parameterClassName)) {
				arguments[i] = (short)0;
			} else if("int".equals(parameterClassName)) {
				arguments[i] = (int)0;
			} else if("long".equals(parameterClassName)) {
				arguments[i] = (long)0;
			} else if("float".equals(parameterClassName)) {
				arguments[i] = (float)0;
			} else if("double".equals(parameterClassName)) {
				arguments[i] = (double)0;
			} else if("boolean".equals(parameterClassName)) {
				arguments[i] = false;
			} else if("char".equals(parameterClassName)) {
				arguments[i] = '\u0000';
			}
		}
		return arguments;
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
	
	public Map<Method, Entry<Object, Object[]>> getSubscribes() {
		return subscribes;
	}
}
