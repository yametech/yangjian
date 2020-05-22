/*
 * Copyright 2020 yametech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yametech.yangjian.agent.core.metric;

import com.yametech.yangjian.agent.api.IMetricMatcher;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.interceptor.IDisableConfig;
import com.yametech.yangjian.agent.core.metric.base.MetricEventBus;

public class BaseConvertAOP implements IDisableConfig {
	protected IMetricMatcher metricMatcher;
	protected Object convert;
	protected MetricEventBus metricEventBus;
	protected String type;
	
	void init(IMetricMatcher metricMatcher, Object convert, MetricEventBus metricEventBus, String type) {
		this.metricMatcher = metricMatcher;
		this.convert = convert;
		this.metricEventBus = metricEventBus;
		this.type = type;
	}

	@Override
	public String disableKey() {
		return Constants.DISABLE_SPI_KEY_PREFIX + metricMatcher.getClass().getSimpleName();
	}
}
