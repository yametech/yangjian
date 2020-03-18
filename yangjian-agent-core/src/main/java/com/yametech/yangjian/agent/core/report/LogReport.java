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
package com.yametech.yangjian.agent.core.report;

import java.util.Map;
import java.util.Map.Entry;

import com.yametech.yangjian.agent.api.IReport;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.core.config.Config;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

public class LogReport implements IReport {
	private static ILogger log = LoggerFactory.getLogger(LogReport.class);
	private static final char URL_SPLIT = '/';
	
	@Override
	public boolean report(String dataType, Long second, Map<String, Object> params) {
		if(dataType == null || dataType.contains("?")) {
			throw new RuntimeException("dataType参数错误（不能为null且不能包含？）");
		}
		if(second == null) {
			second = System.currentTimeMillis() / 1000;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(Config.SERVICE_NAME.getValue()).append(URL_SPLIT)
				.append(second).append(URL_SPLIT).append(dataType);
		if(params == null || params.isEmpty()) {
			log.info(builder.toString());
			return true;
		}
		builder.append('?');
		for(Entry<String, Object> param : params.entrySet()) {
			builder.append(encode(param.getKey())).append('=').append(encode(param.getValue())).append('&');
		}
		builder.deleteCharAt(builder.length() - 1);
		log.info(builder.toString());
		return true;
	}

	private static String encode(Object value) {
		if(value == null) {
			return "";
		}
		return StringUtil.encode(value.toString());
	}
	
	@Override
	public String type() {
		return "log";
	}

}
