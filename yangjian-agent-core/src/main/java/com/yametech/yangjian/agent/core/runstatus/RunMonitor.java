package com.yametech.yangjian.agent.core.runstatus;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.common.CoreConstants;
import com.yametech.yangjian.agent.core.metric.MetricData;
import com.yametech.yangjian.agent.core.report.AsyncReportManage;

public class RunMonitor implements ISchedule, IAppStatusListener, IConfigReader {
	private static final ILogger LOG = LoggerFactory.getLogger(RunMonitor.class);
	private static final String CONFIG_KEY = "metricOutput.interval.heartbeat";
	private static IReportData report = AsyncReportManage.getReport("runStatus");
	private volatile boolean isStop = false;
	private int interval = 10;
	
	@Override
    public Set<String> configKey() {
        return new HashSet<>(Arrays.asList(CONFIG_KEY.replaceAll("\\.", "\\\\.")));
    }

    @Override
    public void configKeyValue(Map<String, String> kv) {
        if (kv == null) {
            return;
        }
        
        String intervalStr = kv.get(CONFIG_KEY);
    	if(intervalStr != null) {
    		try {
    			interval = Integer.parseInt(intervalStr);
            } catch(Exception e) {
            	LOG.warn("{}配置错误：{}", CONFIG_KEY, intervalStr);
            }
    	}
    }
	
	@Override
	public int interval() {
		return interval;
	}

	@Override
	public void execute() {
		synchronized (this) {
			if(isStop) {
				return;
			}
			if(!report.report(MetricData.get(CoreConstants.BASE_PATH_STATUS + Constants.Status.HEARTBEAT))) {
				LOG.warn("心跳上报失败");
			}
		}
	}
	
	/**
	 * 启动状态上报
	 */
	@Override
	public void beforeRun() {
		if(!report.report(MetricData.get(CoreConstants.BASE_PATH_STATUS + Constants.Status.STARTING))) {
			LOG.warn("启动状态上报失败");
		}
	}
	
	/**
	 * 关闭状态上报
	 */
	@Override
	public boolean shutdown(Duration duration) {
		synchronized (this) {
			isStop = true;
			// 异步上报队列一定放到最后关闭防止此处发布失败
			boolean success = report.report(MetricData.get(CoreConstants.BASE_PATH_STATUS + Constants.Status.STARTING));
			if(!success) {
				LOG.warn("启动状态上报失败");
			}
			return success;
		}
	}
	
	@Override
	public int weight() {
		return IAppStatusListener.super.weight() + 30;// 此处加大权重为了后关闭，不是必须
	}
	
}
