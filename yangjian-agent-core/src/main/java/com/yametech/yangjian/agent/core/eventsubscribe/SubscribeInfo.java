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
