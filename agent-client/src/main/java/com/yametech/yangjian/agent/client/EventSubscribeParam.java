package com.yametech.yangjian.agent.client;

import java.util.Map;

public class EventSubscribeParam {
    private static final int MAX_SIZE = 50;// 最多只能放50个数据，如果超出，则不再放入，丢弃最新的
    protected static final ThreadLocal<Map<String, Object>> EVENT_PARAMS = new ThreadLocal<>();

    public static void set(String key, Object value) {
        Map<String, Object> params = EVENT_PARAMS.get();
        if(params == null || params.size() >= MAX_SIZE) {
            return;
        }
        params.put(key, value);
    }

    public static void remove(String key) {
        Map<String, Object> params = EVENT_PARAMS.get();
        if(params != null) {
            params.remove(key);
        }
    }
}
