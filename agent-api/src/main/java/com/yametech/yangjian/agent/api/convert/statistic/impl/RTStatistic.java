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
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.convert.statistic.IStatistic;
import com.yametech.yangjian.agent.api.convert.statistic.StatisticType;

public class RTStatistic extends BaseStatistic {
	private final LongAdder num = new LongAdder();// 当前秒数的总调用次数
	private final LongAdder errorNum = new LongAdder();// 当前秒数的总异常次数
	private final LongAdder total = new LongAdder();// 当前秒数的总耗时（毫秒）
	private final AtomicLong min = new AtomicLong(-1);// 当前秒数的最小耗时（毫秒）
	private final AtomicLong max = new AtomicLong(-1);// 当前秒数的最大耗时（毫秒）

	@Override
	public void combine(TimeEvent timeEvent) {
		this.num.add(timeEvent.getNumber());
		this.errorNum.add(timeEvent.getErrorNum());
		this.total.add(timeEvent.getUseTime());
		long useTimeEach = timeEvent.getUseTime() / timeEvent.getNumber();
		setMax(useTimeEach);
		setMin(useTimeEach);
	}

	@Override
	public void combine(IStatistic statistic) {
		if(!(statistic instanceof RTStatistic)) {
			return;
		}
		RTStatistic rt = (RTStatistic) statistic;
		this.num.add(rt.num.longValue());
		this.errorNum.add(rt.errorNum.longValue());
		this.total.add(rt.total.longValue());
		setMax(rt.max.get());
		setMin(rt.min.get());
	}

	@Override
	protected void clear() {
		num.reset();
		errorNum.reset();
		total.reset();
		min.set(-1);
		max.set(-1);
	}

	@Override
	public Map<String, Object> statisticKV() {
		Map<String, Object> kvs = new HashMap<>();
		kvs.put("num", num.sum());
		kvs.put("error_total", errorNum.sum());
		kvs.put("rt_total", total.sum());
		kvs.put("rt_min", min.get());
		kvs.put("rt_max", max.get());
		return kvs;
	}

	@Override
	public StatisticType statisticType() {
		return StatisticType.RT;
	}

	private void setMin(long min) {
		while(true) {
			long currentValue = this.min.get();
			if((currentValue != -1 && currentValue <= min)
					|| this.min.compareAndSet(currentValue, min)) {
				break;
			}
		}
	}

	private void setMax(long max) {
		while(true) {
			long currentValue = this.max.get();
			if((currentValue != -1 && currentValue > max)
					|| this.max.compareAndSet(currentValue, max)) {
				break;
			}
		}
	}

	@Override
	public String toString() {
		return super.toString() + " : " + num.sum() + "	" + errorNum.sum() + "	" + total.sum() + "	" + min.get() + " " + max.get();
	}

}
