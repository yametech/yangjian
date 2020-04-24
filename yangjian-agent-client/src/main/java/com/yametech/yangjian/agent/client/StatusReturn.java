package com.yametech.yangjian.agent.client;

public class StatusReturn {
	private StatusType type;
	private String reason;

	public StatusReturn(StatusType type, String reason) {
		this.type = type;
		this.reason = reason;
	}
	
	public StatusType getType() {
		return type;
	}

	public void setType(StatusType type) {
		this.type = type;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public static StatusReturn ok() {
		return ok(null);
	}
	
	public static StatusReturn ok(String reason) {
		return new StatusReturn(StatusType.OK, reason);
	}
	
	public static StatusReturn error(String reason) {
		return new StatusReturn(StatusType.ERROR, reason);
	}
	
}
