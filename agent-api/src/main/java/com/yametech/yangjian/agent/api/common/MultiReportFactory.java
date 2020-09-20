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
package com.yametech.yangjian.agent.api.common;

import java.lang.reflect.InvocationTargetException;

import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

public class MultiReportFactory {
	private static final ILogger LOG = LoggerFactory.getLogger(MultiReportFactory.class);
	private static final String ASYNC_KEY = "async";
	private MultiReportFactory() {}
	
	public static IReportData getReport(String reportConfigKey) {
		try {
			if(Config.getKv(Constants.REPORT_CONFIG_KEY_PREFIX + "." + reportConfigKey + "." + ASYNC_KEY) != null) {
					return (IReportData) Class.forName("com.yametech.yangjian.agent.core.report.AsyncMultiReport")
							.getConstructor(String.class).newInstance(reportConfigKey + "." + ASYNC_KEY);
			} else {
				IReportData reportData = (IReportData) Class.forName("com.yametech.yangjian.agent.core.report.MultiReport")
						.getConstructor(String.class).newInstance(reportConfigKey);
				InstanceManage.registryInit(reportData);
				return reportData;
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			LOG.warn(e, "初始化IReportData异常：{}", reportConfigKey);
			throw new RuntimeException(e);
		}
    }
}