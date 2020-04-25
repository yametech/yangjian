package com.yametech.yangjian.agent.core.report.async;

import java.util.List;

import com.yametech.yangjian.agent.api.base.IReportData;

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

}
