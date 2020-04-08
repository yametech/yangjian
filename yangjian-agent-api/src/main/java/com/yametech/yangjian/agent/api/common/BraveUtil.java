package com.yametech.yangjian.agent.api.common;

import java.util.Map;

import brave.propagation.Propagation.Getter;
import brave.propagation.Propagation.Setter;

public class BraveUtil {
	public static final Getter<Map<String, String>, String> MAP_GETTER = (carrier, key) -> carrier.get(key);
	public static final Setter<Map<String, String>, String> MAP_SETTER = (carrier, key, value) -> carrier.put(key, value);
	
}
