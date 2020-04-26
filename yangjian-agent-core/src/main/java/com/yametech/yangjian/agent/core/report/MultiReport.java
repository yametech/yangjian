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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.IReport;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.core.InstanceManage;

public class MultiReport implements IReportData, IConfigReader {
	private static final ILogger LOG = LoggerFactory.getLogger(MultiReport.class);
	static final String CONFIG_KEY_PREFIX = "report";
	private Set<String> configKeys;
	private String myKey;
	private List<IReport> reports;
	
	public MultiReport(String reportConfigKey) {
		if(reportConfigKey == null) {
			throw new IllegalArgumentException("reportConfigKey不能为null");
		}
		this.myKey = CONFIG_KEY_PREFIX + "." + reportConfigKey;
		this.configKeys = new HashSet<>(Arrays.asList(CONFIG_KEY_PREFIX.replaceAll("\\.", "\\\\."), myKey.replaceAll("\\.", "\\\\.")));
	}
	
	/**
	 * 根据Class获取上报实例，并注册配置通知
	 * @param cls	一般为调用的类Class，用于读取配置
	 * @return
	 */
    static IReportData getReport(String reportConfigKey) {
    	MultiReport report = new MultiReport(reportConfigKey);
    	InstanceManage.registryInit(report);
    	return report;
    }
	
	@Override
	public boolean report(Object data) {
		List<IReport> useReports = reports;
		if(useReports == null || useReports.isEmpty()) {
			LOG.warn("{}不存在report", myKey);
			return false;
		}
		for(IReport report : useReports) {
			if(!report.report(data)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean batchReport(List<Object> datas) {
		List<IReport> useReports = reports;
		if(useReports == null || useReports.isEmpty()) {
			LOG.warn("{}不存在report", myKey);
			return false;
		}
		for(IReport report : useReports) {
			if(!report.batchReport(datas)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public Set<String> configKey() {
		return configKeys;
	}

	@Override
	public void configKeyValue(Map<String, String> kv) {
		String typeConfig = kv.get(myKey);
		if(typeConfig == null) {
			typeConfig = kv.get(CONFIG_KEY_PREFIX);
		}
		List<String> types = Arrays.asList(typeConfig.split(","));
		List<IReport> myReports = new ArrayList<>();
		for(IReport report : InstanceManage.listInstance(IReport.class)) {
			if(types.contains(report.type())) {
				myReports.add(report);
			}
		}
		this.reports = myReports;
	}
	
}
