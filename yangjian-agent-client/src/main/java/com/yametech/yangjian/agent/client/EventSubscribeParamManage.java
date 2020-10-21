package com.yametech.yangjian.agent.client;

import java.util.HashMap;
import java.util.Map;

/**
 * 该类仅内部使用
 */
public final class EventSubscribeParamManage extends EventSubscribeParam {

    /**
     * 设置初始化缓存
     */
    public static void start() {
        EVENT_PARAMS.set(new HashMap<>());
    }

    /**
     * 删除缓存，并返回之前的值
     */
    public static Map<String, Object> stop() {
        Map<String, Object> params = EVENT_PARAMS.get();
        EVENT_PARAMS.remove();
        return params;
    }
}
