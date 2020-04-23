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
import com.yametech.yangjian.agent.core.core.InstanceManage;

public class ReportManage implements IReportData, IConfigReader {
	private Set<String> configKeys;
	private String defaultKey = "report";
	private String myKey;
	private List<IReport> reports;
	
	public ReportManage(String reportConfigKey) {
		if(reportConfigKey == null) {
			throw new IllegalArgumentException("reportConfigKey不能为null");
		}
		this.myKey = defaultKey + "." + reportConfigKey;
		this.configKeys = new HashSet<>(Arrays.asList(defaultKey.replaceAll("\\.", "\\\\."), myKey.replaceAll("\\.", "\\\\.")));
	}
	
	/**
	 * 根据Class获取上报实例，并注册配置通知
	 * @param cls	一般为调用的类Class，用于读取配置
	 * @return
	 */
    public static IReportData getReport(String reportConfigKey) {
    	return getReport(reportConfigKey, true);
    }
    /**
     * 	根据Class获取上报实例，根据needNotify确认是否执行通知
     * @param cls
     * @param needNotifyConfig	true：注册时执行一次配置通知（用于在全局通知(InstanceManage.notifyReaders)之后调用该方法）；false：不执行配置通知（用于在全局通知之前调用该方法）；
     * @return
     */
	private static IReportData getReport(String reportConfigKey, boolean needNotifyConfig) {
    	ReportManage report = new ReportManage(reportConfigKey);
    	InstanceManage.registry(report, needNotifyConfig);
    	return report;
    }
	
	@Override
	public boolean report(Object data) {
		List<IReport> useReports = reports;
		if(useReports == null || useReports.isEmpty()) {
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
			typeConfig = kv.get(defaultKey);
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
