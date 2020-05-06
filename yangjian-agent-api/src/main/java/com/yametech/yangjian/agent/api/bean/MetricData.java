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
package com.yametech.yangjian.agent.api.bean;

import java.time.Instant;
import java.util.Map;
import java.util.Map.Entry;

import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.StringUtil;

public class MetricData {
	private static final char URL_SPLIT = '/';
	
	private String serviceName;
	private long second;
	private String metricType;
	private Map<String, Object> params;
	
	public MetricData() {
		serviceName = Constants.serviceName();
	}
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public long getSecond() {
		return second;
	}

	public void setSecond(long second) {
		this.second = second;
	}

	public String getMetricType() {
		return metricType;
	}

	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	
	public static MetricData get(String metricType) {
		return get(null, metricType, null);
	}
	
	public static MetricData get(Long second, String metricType) {
		return get(second, metricType, null);
	}
	
	public static MetricData get(String metricType, Map<String, Object> params) {
		return get(null, metricType, params);
	}
	
	public static MetricData get(Long second, String metricType, Map<String, Object> params) {
		if(second == null) {
			second = Instant.now().getEpochSecond();
		}
		MetricData metricData = new MetricData();
		metricData.setSecond(second);
		metricData.setMetricType(metricType);
		metricData.setParams(params);
		return metricData; 
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(serviceName).append(URL_SPLIT)
				.append(second).append(URL_SPLIT).append(metricType);
		if(params == null || params.isEmpty()) {
			return builder.toString();
		}
		builder.append('?');
		for(Entry<String, Object> param : params.entrySet()) {
			builder.append(encode(param.getKey())).append('=').append(encode(param.getValue())).append('&');
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	private static String encode(Object value) {
		if(value == null) {
			return "";
		}
		return StringUtil.encode(value.toString());
	}

}
