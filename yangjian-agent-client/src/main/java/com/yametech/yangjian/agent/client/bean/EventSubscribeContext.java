package com.yametech.yangjian.agent.client.bean;

import java.util.Map;

public class EventSubscribeContext {
    private Map<String, Object> extraParams;

    public Map<String, Object> getExtraParams() {
        return extraParams;
    }

    public EventSubscribeContext setExtraParams(Map<String, Object> extraParams) {
        this.extraParams = extraParams;
        return this;
    }
}
