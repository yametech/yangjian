package com.yametech.yangjian.agent.plugin.dubbo.trace;

import java.util.Map;

import com.yametech.yangjian.agent.api.trace.custom.IDubboClientCustom;

public class DubboSpanCustom implements IDubboClientCustom {

	@Override
	public boolean sample(Object[] obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, String> tags(Object[] obj) {
		// TODO Auto-generated method stub
		return null;
	}

}