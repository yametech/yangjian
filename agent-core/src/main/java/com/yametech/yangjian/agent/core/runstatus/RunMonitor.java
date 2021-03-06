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

package com.yametech.yangjian.agent.core.runstatus;

import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.bean.MetricData;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MultiReportFactory;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.common.CoreConstants;
import com.yametech.yangjian.agent.core.util.Util;

import java.time.Duration;
import java.util.*;

public class RunMonitor implements ISchedule, IAppStatusListener, IConfigReader {
	private static final ILogger LOG = LoggerFactory.getLogger(RunMonitor.class);
	private static final String CONFIG_KEY = "metricOutput.interval.heartbeat";
	private static IReportData report = MultiReportFactory.getReport("runStatus");
	private volatile boolean isStop = false;
	private int interval = 10;
	private Map<String, Object> params = new HashMap<>();
	
	@Override
    public Set<String> configKey() {
        return new HashSet<>(Collections.singletonList(CONFIG_KEY.replaceAll("\\.", "\\\\.")));
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
            	LOG.warn("{} config error: {}", CONFIG_KEY, intervalStr);
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
			if(!report.report(MetricData.get(CoreConstants.BASE_PATH_STATUS + Constants.Status.RUNNING, params))) {
				LOG.warn("running status report failed");
			}
		}
	}
	
	/**
	 * 启动状态上报
	 */
	@Override
	public void beforeRun() {
		params.put("ip", Util.getIpAddress(null, null));// TODO 检测线上多IP获取是否正确
		if(!report.report(MetricData.get(CoreConstants.BASE_PATH_STATUS + Constants.Status.STARTING, params))) {
			LOG.warn("start status report failed");
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
			boolean success = report.report(MetricData.get(CoreConstants.BASE_PATH_STATUS + Constants.Status.STOPPING, params));
			if(!success) {
				LOG.warn("shutdown status report failed");
			}
			return success;
		}
	}
	
	@Override
	public int weight() {
		return IAppStatusListener.super.weight() + 5;// 此处加大权重为了后关闭（不是必须），但必须小于AsyncReportManage.publish的权重，保证beforeRun执行前AsyncReportManage.publish已执行beforeRun
	}
	
}
