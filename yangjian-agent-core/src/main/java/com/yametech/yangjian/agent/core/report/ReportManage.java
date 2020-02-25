/**
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
	
	public ReportManage(Class<?> useClass) {
		if(useClass == null) {
			throw new IllegalArgumentException("useClass不能为null");
		}
		this.myKey = defaultKey + "." + useClass.getSimpleName();
		this.configKeys = new HashSet<>(Arrays.asList(defaultKey, myKey));
	}
	
	@Override
	public boolean report(String dataType, Long second, Map<String, Object> params) {
		List<IReport> useReports = reports;
		for(IReport report : useReports) {
			report.report(dataType, second, params);
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
		for(IReport report : InstanceManage.listSpiInstance(IReport.class)) {
			if(types.contains(report.type())) {
				myReports.add(report);
			}
		}
		this.reports = myReports;
	}
	
}
