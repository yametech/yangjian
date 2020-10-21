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

package com.yametech.yangjian.agent.client;

public class StatusReturn {
	private MonitorLevel level;
	private String reason;

	public StatusReturn(MonitorLevel level, String reason) {
		this.level = level;
		this.reason = reason;
	}
	
	public MonitorLevel getLevel() {
		return level;
	}

	public void setType(MonitorLevel level) {
		this.level = level;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public static StatusReturn info(String reason) {
		return new StatusReturn(MonitorLevel.INFO, reason);
	}
	
	public static StatusReturn warn(String reason) {
		return new StatusReturn(MonitorLevel.WARN, reason);
	}
	
	public static StatusReturn error(String reason) {
		return new StatusReturn(MonitorLevel.ERROR, reason);
	}
	
}
