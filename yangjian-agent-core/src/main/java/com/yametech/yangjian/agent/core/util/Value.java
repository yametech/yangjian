package com.yametech.yangjian.agent.core.util;

public class Value<T> {
	private T value;
	
	private Value() {}
	
	private Value(T value) {
		this.value = value;
	}
	
	public static <T> Value<T> absent() {
		return new Value<>();
	}
	
	public static <T> Value<T> of(T value) {
		return new Value<>(value);
	}
	
	public void set(T value) {
		this.value = value;
	}
	
	public T get() {
		return value;
	}
	
	public T get(T defaultValue) {
		return value == null ? defaultValue : value;
	}
}
