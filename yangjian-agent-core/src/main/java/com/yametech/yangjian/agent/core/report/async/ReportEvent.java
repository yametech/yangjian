package com.yametech.yangjian.agent.core.report.async;

import java.util.List;

import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.core.util.Util;

public class ReportEvent {
	private String reportType;
	private IReportData report;
	private List<Object> datas;
	
	public void reset(String reportType, IReportData report, List<Object> datas) {
		this.reportType = reportType;
		this.report = report;
		this.datas = datas;
	}
	
	public void call() {
		report.batchReport(datas);
	}
	
	public String getReportType() {
		return reportType;
	}
	
	@Override
	public String toString() {
		return reportType + "[" + Util.join(",", datas) + "]";
	}

}
