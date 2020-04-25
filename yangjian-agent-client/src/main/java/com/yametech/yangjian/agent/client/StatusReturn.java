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
