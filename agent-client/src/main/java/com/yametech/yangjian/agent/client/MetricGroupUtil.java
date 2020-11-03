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

import java.util.function.Supplier;

public class MetricGroupUtil {
	private MetricGroupUtil() {}

	/**
	 * @param groupName	分组名称，数量可枚举，需提前通知监控研发初始化分组值，否则不生效
	 * @param name	统计标识，同名会聚合计算qps/rt，长度不可超过50，如：success-order、error-order
	 */
	public static void mark(String groupName, String name) {
		mark(groupName, name, 1);
	}

	/**
	 * @param groupName	分组名称，数量可枚举，需提前通知监控研发初始化分组值，否则不生效
	 * @param name	统计标识，同名会聚合计算qps/rt，长度不可超过50，如：success-order、error-order
	 * @param number	累加次数
	 */
	public static void mark(String groupName, String name, int number) {}

	/**
	 * @param groupName	分组名称，数量可枚举，需提前通知监控研发初始化分组值，否则不生效
	 * @param name	统计标识，同名会聚合计算qps/rt，长度不可超过50，如：success-order、error-order
	 * @param supplier	执行的业务
	 * @param <T>
	 * @return
	 */
	public static <T> T mark(String groupName, String name, Supplier<T> supplier) {
		return mark(groupName, name, 1, supplier);
	}

	/**
	 * @param groupName	分组名称，数量可枚举，需提前通知监控研发初始化分组值，否则不生效
	 * @param name	统计标识，同名会聚合计算qps/rt，长度不可超过50，如：success-order、error-order
	 * @param number	累加次数
	 * @param supplier	执行的业务
	 * @param <T>
	 * @return
	 */
	public static <T> T mark(String groupName, String name, int number, Supplier<T> supplier) {
		return supplier.get();
	}

}
