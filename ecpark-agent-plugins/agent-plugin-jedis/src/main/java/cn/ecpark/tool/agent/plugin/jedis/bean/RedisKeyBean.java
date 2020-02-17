package cn.ecpark.tool.agent.plugin.jedis.bean;

import java.util.List;

public class RedisKeyBean {
	private List<String> keys;
	private long eventTime;
	private long useTime;

	public RedisKeyBean(List<String> keys, long eventTime, long useTime) {
		this.keys = keys;
		this.eventTime = eventTime;
		this.useTime = useTime;
	}

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	public long getEventTime() {
		return eventTime;
	}

	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}

	public long getUseTime() {
		return useTime;
	}

	public void setUseTime(long useTime) {
		this.useTime = useTime;
	}

}
