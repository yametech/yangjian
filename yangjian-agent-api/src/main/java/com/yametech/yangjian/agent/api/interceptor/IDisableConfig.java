package com.yametech.yangjian.agent.api.interceptor;

/**
 * 用于根据配置识别实例是否禁用
 */
public interface IDisableConfig {

    /**
     *
     * @return  禁用插件的配置key
     */
    String disableKey();
}
