package cn.ecpark.tool.agent.api.convert;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.bean.TimeEvent;

public interface IConvertMatcher {
	
	/**
	 * 初始化convert绑定的metricMatcher
	 * @param metricMatcher 与convert关联的metricMatcher，该参数可用于携带convert需要的数据
	 */
	default void setMetricMatcher(IMetricMatcher metricMatcher) {}
	
	/**
	 * 获取一个默认的TimeEvent
	 * @param useTimeMillis
	 * @return
	 */
	default TimeEvent get(long startTime) {
		TimeEvent event = new TimeEvent();
		long now = System.currentTimeMillis();
		event.setEventTime(now);
        event.setUseTime(now - startTime);
        return event;
	}
}
