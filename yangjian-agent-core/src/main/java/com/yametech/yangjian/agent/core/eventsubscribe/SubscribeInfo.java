package com.yametech.yangjian.agent.core.eventsubscribe;

import java.util.Arrays;

public class SubscribeInfo {
	private Object instance;
	private Object[] defaultArguments;
	private Integer[] extraArgumentsIndex;

	public SubscribeInfo(Object instance, Object[] defaultArguments, Integer[] extraArgumentsIndex) {
		this.instance = instance;
		this.defaultArguments = defaultArguments;
		this.extraArgumentsIndex = extraArgumentsIndex;
	}

	public Object getInstance() {
		return instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}

	public Object[] getDefaultArgumentsCopy() {
		return Arrays.copyOf(defaultArguments, defaultArguments.length);
	}

	public void setDefaultArguments(Object[] defaultArguments) {
		this.defaultArguments = defaultArguments;
	}

	public Integer[] getExtraArgumentsIndex() {
		return extraArgumentsIndex;
	}

	public void setExtraArgumentsIndex(Integer[] extraArgumentsIndex) {
		this.extraArgumentsIndex = extraArgumentsIndex;
	}

}
