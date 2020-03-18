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
package com.yametech.yangjian.agent.core.config;

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
