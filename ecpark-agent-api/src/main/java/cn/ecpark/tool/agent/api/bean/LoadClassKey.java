package cn.ecpark.tool.agent.api.bean;

public class LoadClassKey {
	private String cls;
	private String key;
	
	public LoadClassKey(String cls) {// 使用cls作为key
		this(cls, cls);
	}
	
	public LoadClassKey(String cls, String key) {
		this.cls = cls;
		this.key = key;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		return cls + " : " + key;
	}
	
}
