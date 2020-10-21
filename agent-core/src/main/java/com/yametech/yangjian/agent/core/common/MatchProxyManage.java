package com.yametech.yangjian.agent.core.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.yametech.yangjian.agent.core.metric.MetricMatcherProxy;
import com.yametech.yangjian.agent.core.pool.PoolMonitorMatcherProxy;
import com.yametech.yangjian.agent.core.trace.TraceMatcherProxy;
import com.yametech.yangjian.agent.core.util.Util;

public class MatchProxyManage {
	private static final Map<Class<?>, Class<?>> PROXY = new HashMap<>();
	
	static {
		for(Class<?> matcher : Arrays.asList(MetricMatcherProxy.class, PoolMonitorMatcherProxy.class, TraceMatcherProxy.class)) {// 此处手动维护，后续自动发现
			PROXY.put(Util.superClassGeneric(matcher, 0), matcher);
		}
	}
	
	/**
	 * 
	 * @param cls
	 * @return	返回cls的代理类
	 */
	public static Entry<Class<?>, Class<?>> getProxy(Class<?> cls) {
		for(Entry<Class<?>, Class<?>> entry : PROXY.entrySet()) {
			if(entry.getKey().isAssignableFrom(cls)) {
				return entry;
			}
		}
		return null;
	}

	public static boolean hasProxy(Class<?> cls) {
		return getProxy(cls) != null;
	}
}
