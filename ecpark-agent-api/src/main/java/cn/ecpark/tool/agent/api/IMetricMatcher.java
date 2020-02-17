package cn.ecpark.tool.agent.api;

import cn.ecpark.tool.agent.api.common.Constants;

public interface IMetricMatcher extends InterceptorMatcher {
	
	/**
     * 须指定事件类型，如：dubbo-client、dubbo-server、http-server、redis、mysql、kafka、rabbitmq、mongo、http-client
     */
    default String type() {
        return Constants.EventType.METHOD;
    }
}
