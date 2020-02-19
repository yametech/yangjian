/**
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

package com.yametech.yangjian.agent.api.convert.statistic.impl;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;

import com.yametech.yangjian.agent.api.convert.statistic.IStatistic;
import com.yametech.yangjian.agent.api.convert.statistic.StatisticType;

/**
 * 子类必须包含
 * 
 * @author liuzhao
 */
public abstract class BaseStatistic implements IStatistic {
	private String type;// 事件类型
	private String sign;// 事件唯一标识
	private long second;// 统计的秒数
	private long updateTime;// 数据最近更新事件，在数据输出时会判断该时间与当前时间的差值
	
	public String getType() {
		return type;
	}
	
	public long getSecond() {
		return second;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	
	@Override
	public String toString() {
		return type + "	" + sign + "	" + second;
	}

	@Override
	public final void reset(String type, String sign, long second) {
		this.type = type;
		this.sign = sign;
		this.second = second;
		updateTime = System.currentTimeMillis();
		clear();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final Entry<String, Object>[] kv() {
		Map<String, Object> kvs = statisticKV();
		if(kvs == null || kvs.size() == 0) {
			return null;
		}
		Entry<String, Object>[] entrys = new SimpleEntry[kvs.size() + 1];
		entrys[0] = new SimpleEntry<>("sign", sign);
		int index = 1;
		for(Entry<String, Object> entry : kvs.entrySet()) {
			entrys[index++] = new SimpleEntry<>(entry);
		}
		return entrys;
	}
	
	/**
	 * 清除状态
	 */
	protected abstract void clear();
	
	/**
	 * 统计类型
	 * @return	StatisticType
	 */
	public abstract StatisticType statisticType();
	
	/**
	 * 统计输出key-value
	 * @return	Map
	 */
	public abstract Map<String, Object> statisticKV();

}
