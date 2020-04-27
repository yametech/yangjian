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

import java.util.Map;
import java.util.function.Supplier;

public class TraceUtil {
	private TraceUtil() {}
	
	/**
	 * 标记执行supplier逻辑的链路信息，使用该方法包装的逻辑如果接入了yangjian-agent会自动生成链路节点
	 * @param name	链路span名称
	 * @param supplier	需要执行的逻辑
	 * @return	自定义返回值
	 */
	public static <T> T mark(String name, Supplier<T> supplier) {
		return mark(name, null, supplier);
	}
	
	/**
	 * 	标记执行supplier逻辑的链路信息，使用该方法包装的逻辑如果接入了yangjian-agent会自动生成链路节点
	 * @param name	链路span名称
	 * @param tags	链路标签
	 * @param supplier	需要执行的逻辑
	 * @return	自定义返回值
	 */
	public static <T> T mark(String name, Map<String, String> tags, Supplier<T> supplier) {
		return mark(name, true, tags, supplier);
	}
	
	/**
	 * 	标记执行supplier逻辑的链路信息，使用该方法包装的逻辑如果接入了yangjian-agent会自动生成链路节点
	 * @param name	链路span名称
	 * @param sample	是否生成链路信息，如果为false则不记录链路信息，主要用于根据不同情况决定是否产生链路节点；如果为true，则使用链路的自定义采样率生成链路节点（不一定全采样）
	 * @param tags	链路标签
	 * @param supplier	需要执行的逻辑
	 * @return	自定义返回值
	 */
	public static <T> T mark(String name, boolean sample, Map<String, String> tags, Supplier<T> supplier) {
		return supplier.get();
	}
	
	public static void main(String[] args) {
		System.err.println(mark("test", true, null, () -> "test"));
	}
}
