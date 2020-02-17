package cn.ecpark.tool.agent.core.config;

public class ConfigValue<T> {
	private ValueConvert<T> convert;
	private String key;
	private T value;
	
	public ConfigValue(String key, T value) {
		this.key = key;
		this.value = value;
	}
	
	public ConfigValue(String key, String value, ValueConvert<T> convert) {
		this.convert = convert;
		this.key = key;
		setValueByKey(key, value);
	}
	
//	public void updateKey(String key) {
//		this.key = key;
//	}
	
	private boolean matchKey(String key) {
		return key != null && key.equals(this.key);
	}
	
	public String getKey() {
		return key;
	}
	
	public T getValue() {
		return value;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	
	public boolean setValueByKey(String key, String value) {
		if(!matchKey(key) || value == null) {
			return false;
		}
		this.value = convert.convert(value);
		return true;
	}
	
	public interface ValueConvert<T> {
		T convert(String value);
	}
	
	
}
