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
package com.yametech.yangjian.agent.api.convert.statistic.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.convert.statistic.IStatistic;
import com.yametech.yangjian.agent.api.convert.statistic.StatisticType;

public class QPSStatistic extends BaseStatistic {
	private final LongAdder num = new LongAdder();// 当前秒数的总调用次数
	private final LongAdder errorNum = new LongAdder();// 当前秒数的总异常次数

	@Override
	protected void clear() {
		num.reset();
		errorNum.reset();
	}

	@Override
	public void combine(TimeEvent timeEvent) {
		this.num.add(timeEvent.getNumber());
		this.errorNum.add(timeEvent.getErrorNum());
	}

	@Override
	public void combine(IStatistic statistic) {
		if(!(statistic instanceof QPSStatistic)) {
			return;
		}
		QPSStatistic qps = (QPSStatistic) statistic;
		this.num.add(qps.num.longValue());
		this.errorNum.add(qps.errorNum.longValue());
	}

	@Override
	public Map<String, Object> statisticKV() {
		Map<String, Object> kvs = new HashMap<>();
		kvs.put("num", num.sum());
		kvs.put("error_total", errorNum.sum());
		return kvs;
	}

	@Override
	public StatisticType statisticType() {
		return StatisticType.QPS;
	}

	@Override
	public String toString() {
		return super.toString() + " : " + num.sum() + " " + errorNum.sum();
	}

}
