package cn.ecpark.tool.agent.plugin.mysql.bean;

public class SqlBean {
	private String sql;
	private long eventTime;
	private long useTime;

	public SqlBean(String sql, long eventTime, long useTime) {
		this.sql = sql;
		this.eventTime = eventTime;
		this.useTime = useTime;
	}
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
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
