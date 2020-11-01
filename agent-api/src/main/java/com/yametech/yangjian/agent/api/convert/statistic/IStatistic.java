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
package com.yametech.yangjian.agent.api.convert.statistic;

import java.util.Map.Entry;

import com.yametech.yangjian.agent.api.bean.TimeEvent;

public interface IStatistic {
	
	/**
	 * 合并原始数据
	 * @param timeEvent	事件对象
	 */
	void combine(TimeEvent timeEvent);

	/**
	 * 合并其他统计数据
	 * @param statistic
	 */
	void combine(IStatistic statistic);
	
	/**
	 * 重置数据
	 * @param type	类型
	 * @param identify	标识
	 * @param second	秒数
	 */
	void reset(String type, String identify, long second);
	
	/**
	 * @return	当前统计的数据key-value
	 */
	Entry<String, Object>[] kv();
}
