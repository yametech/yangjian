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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.bean.ConfigNotifyType;
import com.yametech.yangjian.agent.api.common.InstanceManage;
import com.yametech.yangjian.agent.api.interceptor.IConstructorListener;
import com.yametech.yangjian.agent.api.interceptor.IMethodAOP;
import com.yametech.yangjian.agent.api.interceptor.IStaticMethodAOP;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.eventsubscribe.eventbus.SubscribeEventBus;
import com.yametech.yangjian.agent.core.util.Util;

public class EventDispatcher implements IMethodAOP<Object>, IConstructorListener, IStaticMethodAOP<Object>, IConfigReader {
	private static final ILogger LOG = LoggerFactory.getLogger(EventDispatcher.class);
	private static final String CONFIG_KEY_CHECK_SIZE = "eventSubscribe.check.minSize";
	static final String CONFIG_KEY_CALL_ASYNC = "eventSubscribe.callAsync";
	private static SubscribeEventBus subscribeEventBus;
	private int minCheckStackSize = 10;// 死循环检测最小调用栈大小
	private boolean callAsync = true;// 是否异步通知
	private EventSubscribe eventSubscribe;
	
	@Override
	public Set<String> configKey() {
		return new HashSet<>(Arrays.asList(CONFIG_KEY_CHECK_SIZE.replaceAll("\\.", "\\\\."), CONFIG_KEY_CALL_ASYNC.replaceAll("\\.", "\\\\.")));
	}
	
	@Override
	public void configKeyValue(Map<String, String> kv) {
		if(kv.containsKey(CONFIG_KEY_CHECK_SIZE)) {
			try {
    			this.minCheckStackSize = Integer.parseInt(kv.get(CONFIG_KEY_CHECK_SIZE));
            } catch(Exception e) {
            	LOG.warn("{}配置错误：{}", CONFIG_KEY_CHECK_SIZE, kv.get(CONFIG_KEY_CHECK_SIZE));
            }
		}
		if(kv.containsKey(CONFIG_KEY_CALL_ASYNC)) {
			try {
    			this.callAsync = Boolean.parseBoolean(kv.get(CONFIG_KEY_CALL_ASYNC));
            } catch(Exception e) {
            	LOG.warn("{}配置错误：{}", CONFIG_KEY_CALL_ASYNC, kv.get(CONFIG_KEY_CALL_ASYNC));
            }
		}
	}
	
	@Override
	public ConfigNotifyType notifyType() {
		return ConfigNotifyType.ALWAYS;
	}

	void init(EventSubscribe eventSubscribe) {
		this.eventSubscribe = eventSubscribe;
	}
	
	@Override
	public Object after(Object[] allArguments, Method method, BeforeResult<Object> beforeResult, Object ret, Throwable t,
			Map<Class<?>, Object> globalVar) throws Throwable {
		notify(null, allArguments, method, ret, t);
		return ret;
	}

	@Override
	public void constructor(Object thisObj, Object[] allArguments) throws Throwable {
		notify(thisObj, allArguments, null, null, null);
	}

	@Override
	public Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult<Object> beforeResult,
			Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
		notify(thisObj, allArguments, method, ret, t);
		return ret;
	}
	
	private void notify(Object sourceObj, Object[] allArguments, Method method, Object ret, Throwable t) {
		if(eventSubscribe != null && !check()) {
			if(callAsync) {
				getEventBus().publish(event -> event.reset(eventSubscribe, sourceObj, allArguments, method, ret, t));
			} else {
				eventSubscribe.notify(sourceObj, allArguments, method, ret, t);
			}
		}
	}
	
	private SubscribeEventBus getEventBus() {
		if(subscribeEventBus != null) {
			return subscribeEventBus;
		}
		synchronized (EventDispatcher.class) {
			if(subscribeEventBus != null) {
				return subscribeEventBus;
			}
			subscribeEventBus = new SubscribeEventBus();
			InstanceManage.registryInit(subscribeEventBus);
			return subscribeEventBus;
		}
	}
	
	/**
	 * 检测是否出现死循环
	 * @return
	 */
	private boolean check() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		if(stackTraceElements.length <= minCheckStackSize) {// 降低检测性能损耗
			return false;
		}
        for (StackTraceElement element : stackTraceElements) {
        	if(EventSubscribe.class.getName().equals(element.getClassName()) && "notify".equals(element.getMethodName())) {
        		LOG.warn("因事件订阅代码实现错误导致死循环（屏蔽多余调用）：{}.{} - {}", eventSubscribe.getClassName(), eventSubscribe.getMethodName(), Util.join(",", eventSubscribe.getSubscribes().keySet()));
        		return true;
        	}
        }
        return false;
	}
	
	@Override
	public BeforeResult<Object> before(Object[] allArguments, Method method) throws Throwable {
		return null;
	}
	
	@Override
	public BeforeResult<Object> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
		return null;
	}


}
