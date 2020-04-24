package com.yametech.yangjian.agent.core.trace;

import java.time.Duration;

import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.core.core.InstanceManage;
import com.yametech.yangjian.agent.core.trace.base.BraveHelper;
import com.yametech.yangjian.agent.core.trace.base.TraceEventBus;

public class TraceInit implements IAppStatusListener {

	@Override
	public void beforeRun() {
		InstanceManage.registry(new TraceEventBus());
		InstanceManage.registry(BraveHelper.getTracing(span -> InstanceManage.getInstance(TraceEventBus.class).publish(t -> t.setSpan(span)), null));
	}

	@Override
	public boolean shutdown(Duration duration) {
		return true;
	}
	
}
