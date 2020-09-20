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

package com.yametech.yangjian.agent.core.report.async;

import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.util.Util;

import java.util.List;

public class ReportEvent {
	private static final ILogger LOG = LoggerFactory.getLogger(ReportEvent.class);
	private String reportType;
	private IReportData report;
	private List<Object> datas;
	
	public void reset(String reportType, IReportData report, List<Object> datas) {
		this.reportType = reportType;
		this.report = report;
		this.datas = datas;
	}
	
	public void call() {
		boolean success = report.batchReport(datas);
		if(!success) {
			LOG.warn("async report failed: {}", Util.join(" , ", datas));
		}
	}
	
	public String getReportType() {
		return reportType;
	}
	
	@Override
	public String toString() {
		return reportType + "[" + Util.join(",", datas) + "]";
	}

}
