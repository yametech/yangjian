package com.yametech.yangjian.agent.core.eventsubscribe;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

public class BindManage {
	private static final ILogger LOG = LoggerFactory.getLogger(BindManage.class);
	private static final int MAX_EVENT = 200;
	private static final int MAX_SUBSCRIBES_EACH_GROUP = 50;
	private static Map<String, EventSubscribe> events = new ConcurrentHashMap<>();// 事件源方法描述与EventSubscribe的关系
	private static Map<String, Set<EventSubscribe>> eventSubscribes = new ConcurrentHashMap<>();// 事件分组与EventSubscribe的关系
	private static Map<String, Map<Method, Object>> subscribes = new ConcurrentHashMap<>();// 事件分组与订阅方法及实例的关系
	
	private BindManage() {}
	
	/**
	 * 注册事件并返回注册对象，重复注册按照methodDefined判断仅第一次生效
	 * @param methodDefined
	 * @return
	 */
	public static EventSubscribe registEvent(String eventGroup, MethodDefined methodDefined) {
		LOG.info("registEvent:{} - {}", eventGroup, methodDefined);
		String methodKey = methodDefined.getMethodDes();
		EventSubscribe eventSubscribe = events.computeIfAbsent(methodKey, key -> {
			if(events.size() > MAX_EVENT) {
				return null;
			}
			boolean ignoreParams = eventGroup.indexOf(".ignoreParams.") != -1;
			return new EventSubscribe(ignoreParams, methodDefined.getClassDefined().getClassName(), 
					methodDefined.getMethodName(), methodDefined.getParams(), methodDefined.getMethodRet());
		});
		if(eventSubscribe == null) {
			return null;
		}
		Set<EventSubscribe> groupSubscribes = eventSubscribes.computeIfAbsent(eventGroup, key -> new CopyOnWriteArraySet<>());
		groupSubscribes.add(eventSubscribe);
		refreshBind(eventGroup, eventSubscribe);
		return eventSubscribe;
	}
	
	/**
	 * 	注册监听，重复注册按照eventGroup + method判断仅第一次生效
	 * @param eventGroup
	 * @param method
	 * @param instance
	 */
	public static void registSubscribe(String eventGroup, Method method, Object instance) {
		LOG.info("registSubscribe:{} - {} - {}", eventGroup, method, instance);
		Map<Method, Object> groupSubscribe = subscribes.computeIfAbsent(eventGroup, key -> new ConcurrentHashMap<>());
		Object value = groupSubscribe.computeIfAbsent(method, key -> {
			if(groupSubscribe.size() > MAX_SUBSCRIBES_EACH_GROUP) {
				return null;
			}
			return instance;
		});
		if(value == null) {
			return;
		}
		Set<EventSubscribe> subscribes = eventSubscribes.get(eventGroup);
		if(subscribes != null) {
			subscribes.forEach(eventSubscribe -> refreshBind(eventGroup, eventSubscribe));
		}
	}
	
	/**
	 * 刷新绑定关系
	 * @param eventGroup
	 * @param eventSubscribe
	 */
	private static void refreshBind(String eventGroup, EventSubscribe eventSubscribe) {
		Map<Method, Object> subscribeMethod = subscribes.get(eventGroup);
		if(subscribeMethod != null && subscribeMethod.size() > 0) {
			subscribeMethod.forEach(eventSubscribe::regist);
		}
	}
	
}
