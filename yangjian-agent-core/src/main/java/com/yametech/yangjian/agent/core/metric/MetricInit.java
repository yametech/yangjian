package com.yametech.yangjian.agent.core.metric;

import java.time.Duration;

import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.core.core.InstanceManage;
import com.yametech.yangjian.agent.core.metric.base.MetricEventBus;

public class MetricInit implements IAppStatusListener {

	@Override
	public void beforeRun() {
		InstanceManage.registry(new MetricEventBus());
	}

	@Override
	public boolean shutdown(Duration duration) {
		return true;
	}
	
}
