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
package com.yametech.yangjian.agent.core.eventsubscribe.base;

import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.common.MethodUtil;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.client.bean.EventSubscribeContext;
import com.yametech.yangjian.agent.core.util.RateLimit;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class EventSubscribe {
	private static final ILogger LOG = LoggerFactory.getLogger(EventSubscribe.class);
	private static final int REGISTER_MAX_NUM = 100;// 最大注册个数(subscribes的最大长度)
	private static final RateLimit LIMITER = RateLimit.create(10);
	private final Map<String, Integer> extraArgumentIndex = new HashMap<>();
	private final Map<Method, SubscribeInfo> subscribes = new ConcurrentHashMap<>();
	private final MethodDefined methodDefined;
	private final String className;
	private final String methodName;
	private final String[] params;

	public EventSubscribe(MethodDefined methodDefined) {
		this.methodDefined = methodDefined;
		this.className = methodDefined.getClassDefined().getClassName();
		this.methodName = methodDefined.getMethodName();
		this.params = methodDefined.getParams();
		extraArgumentIndex.put(this.className, 0);
		extraArgumentIndex.put(methodDefined.getMethodRet(), 1);
		extraArgumentIndex.put(Throwable.class.getTypeName(), 2);
		extraArgumentIndex.put(EventSubscribeContext.class.getTypeName(), 3);
	}

	/**
	 * 通知注册方
	 * 注意 : 该方法名不允许改动，EventDispatcher类中有检测使用
	 * @param sourceObj
	 * @param allArguments
	 * @param method
	 * @param ret
	 * @param t
	 */
	public void notify(Object sourceObj, Object[] allArguments, Map<String, Object> extraParams, Method method, Object ret, Throwable t) {
		if(subscribes.size() == 0) {
			return;
		}
		subscribes.forEach((subscribeMethod, instance) -> {
			try {
				subscribeMethod.invoke(instance.getInstance(), getArguments(instance, subscribeMethod, sourceObj, allArguments, ret, t, extraParams));
			} catch (Exception e) {
				if(LIMITER.tryAcquire()) {
					LOG.warn(e, "event subscribe consume exception: {} {}", method, subscribeMethod);
				}
			}
		});
	}

	/**
	 * 获取订阅方法的参数
	 * @param info
	 * @param subscribeMethod
	 * @param sourceObj
	 * @param arguments
	 * @param ret
	 * @param t
	 * @return
	 */
	private Object[] getArguments(SubscribeInfo info, Method subscribeMethod, Object sourceObj, Object[] arguments, Object ret, Throwable t, Map<String, Object> params) {
		EventSubscribeContext context = new EventSubscribeContext().setExtraParams(params);
		if(info.isIgnoreParams()) {
			return setExtraParams(info, info.getDefaultArgumentsCopy(), sourceObj, ret, t, context);// 注意：后几个参数的顺序不能变，必须与extraArgumentIndex中put的序号一致
		}
		int argLength = arguments == null ? 0 : arguments.length;
		if(subscribeMethod.getParameterCount() == argLength) {
			return arguments;
//		} else if(method.getParameterCount() < argLength) {// 注册时有检查不会发生
//			return null;
		}
		Object[] newArguments = info.getDefaultArgumentsCopy();
		if(argLength > 0) {
			System.arraycopy(arguments, 0, newArguments, 0, argLength);
		}
		return setExtraParams(info, newArguments, sourceObj, ret, t, context);// 注意：后几个参数的顺序不能变，必须与extraArgumentIndex中put的序号一致
	}
	
	private Object[] setExtraParams(SubscribeInfo info, Object[] newArguments, Object... extraArguments) {
		int startIndex = params.length;
		if(info.isIgnoreParams()) {
			startIndex = 0;
		}
		for(int i = startIndex; i < newArguments.length; i++) {
			newArguments[i] = extraArguments[info.getExtraArgumentsIndex()[i - startIndex]];
		}
		return newArguments;
//		boolean sourceObjInit = false;
//		boolean throwableInit = false;
//		boolean returnInit = false;
//		for(int i = startIndex; i < method.getParameterCount(); i++) {
//			if(t != null && Throwable.class.isAssignableFrom(method.getParameterTypes()[i]) && !throwableInit) {
//				newArguments[i] = t;
//				throwableInit = true;
//			} else if(sourceObj.getClass().isAssignableFrom(method.getParameterTypes()[i]) && !sourceObjInit) {
//				newArguments[i] = sourceObj;
//				sourceObjInit = true;
//			} else if(ret != null && ret.getClass().isAssignableFrom(method.getParameterTypes()[i]) && !returnInit) {
//				newArguments[i] = ret;
//				returnInit = true;
//			}
//		}
	}
	
	/**
	 * 注册事件监听(按照method去重)，并检测：参数是否匹配、监听方与事件方是否一致；
	 * @param method
	 * @param instance
	 * @return
	 */
	public boolean register(Method method, Object instance, boolean ignoreParams) {
		if(subscribes.containsKey(method)) {
			return true;
		}
		if(MethodUtil.getId(method).equals(MethodUtil.getId(methodDefined))) {
			LOG.warn("not allow self subscribe: {}", method);
			return false;
		}
		if(!ignoreParams && method.getParameterTypes().length < params.length) {
			LOG.warn("params number not match: {} - {}  > {}", method.getParameterTypes().length, params.length, method);
			return false;
		}
		if(method.getDeclaringClass().getTypeName().equals(className) && method.getName().equals(methodName)) {
			LOG.warn("订阅方法与被订阅方法不能一样：{}", method);
			return false;
		}
		int startCheckIndex = 0;
		if(!ignoreParams) {
			for(int i = 0; i < params.length; i++) {
				if(!params[i].equals(method.getParameterTypes()[i].getTypeName())) {
					LOG.warn("第{}个参数类型不匹配应该为{}，实际为{}，method={}", i + 1, params[i], method.getParameterTypes()[i].getTypeName(), method);
					return false;
				}
			}
			startCheckIndex = params.length;
		}
		if(method.getParameterCount() - startCheckIndex > 3) {
			LOG.warn("subscribe method params number error: {}", method);
			return false;
		}
		List<Integer> index = new ArrayList<>();
		Set<String> extraParamsType = new HashSet<>(extraArgumentIndex.keySet());
		for(int i = startCheckIndex; i < method.getParameterCount(); i++) {
			String typeName = method.getParameterTypes()[i].getTypeName();
			if(!extraParamsType.remove(typeName)) {
				LOG.warn("订阅方法的第{}个参数类型不匹配：{}，{}", i, typeName, method);
				return false;
			}
			index.add(extraArgumentIndex.get(typeName));
		}
		if(subscribes.size() > REGISTER_MAX_NUM) {
			LOG.warn("subscribe error {}, too many subscribe {}", method, REGISTER_MAX_NUM);
			return false;
		}
		subscribes.put(method, new SubscribeInfo(instance, ignoreParams, getDefaultArguments(method), index.toArray(new Integer[0])));
		LOG.info("subscribe success: {}.{} - {}", className, methodName, method);
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
			String parameterClassName = method.getParameterTypes()[i].getTypeName();
			switch (parameterClassName) {
				case "byte":
					arguments[i] = (byte) 0;
					break;
				case "short":
					arguments[i] = (short) 0;
					break;
				case "int":
					arguments[i] = 0;
					break;
				case "long":
					arguments[i] = (long) 0;
					break;
				case "float":
					arguments[i] = (float) 0;
					break;
				case "double":
					arguments[i] = (double) 0;
					break;
				case "boolean":
					arguments[i] = false;
					break;
				case "char":
					arguments[i] = '\u0000';
					break;
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

	public Map<Method, SubscribeInfo> getSubscribes() {
		return subscribes;
	}
}
