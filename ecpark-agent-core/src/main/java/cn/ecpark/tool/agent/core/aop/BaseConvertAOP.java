package cn.ecpark.tool.agent.core.aop;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.core.aop.base.MetricEventBus;

public class BaseConvertAOP {
	protected Object convert;
	protected MetricEventBus metricEventBus;
	protected IMetricMatcher metricMatcher;
	
	void init(Object convert, MetricEventBus metricEventBus, IMetricMatcher metricMatcher) {
		this.convert = convert;
		this.metricEventBus = metricEventBus;
		this.metricMatcher = metricMatcher;
	}
}
