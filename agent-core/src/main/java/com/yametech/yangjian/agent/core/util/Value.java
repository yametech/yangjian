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
