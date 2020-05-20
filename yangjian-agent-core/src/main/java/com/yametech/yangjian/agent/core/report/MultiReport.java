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

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.IReport;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.bean.ConfigNotifyType;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.InstanceManage;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

import java.util.*;

/**
 * 注意不要修改路径、类名、构造方法，api中有使用反射获取类实例
 * @Description 
 * 
 * @author liuzhao
 * @date 2020年5月6日 下午10:46:21
 */
public class MultiReport implements IReportData, IConfigReader {
	private static final ILogger LOG = LoggerFactory.getLogger(MultiReport.class);
	private Set<String> configKeys;
	private String myKey;
	private List<IReport> reports;

	/**
	 * 有反射调用
	 * @param reportConfigKey
	 */
	public MultiReport(String reportConfigKey) {
		if(reportConfigKey == null) {
			throw new IllegalArgumentException("reportConfigKey不能为null");
		}
		this.myKey = Constants.REPORT_CONFIG_KEY_PREFIX + "." + reportConfigKey;
		this.configKeys = new HashSet<>(Arrays.asList(Constants.REPORT_CONFIG_KEY_PREFIX.replaceAll("\\.", "\\\\."), myKey.replaceAll("\\.", "\\\\.")));
	}
	
	/**
	 * 根据Class获取上报实例，并注册配置通知
	 * @param reportConfigKey	一般为调用的类Class，用于读取配置
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
			typeConfig = kv.get(Constants.REPORT_CONFIG_KEY_PREFIX);
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
	
	@Override
	public ConfigNotifyType notifyType() {
		return ConfigNotifyType.CHANGE;
	}
	
}
