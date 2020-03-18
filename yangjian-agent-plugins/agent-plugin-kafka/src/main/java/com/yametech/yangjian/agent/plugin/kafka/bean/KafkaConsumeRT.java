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
package com.yametech.yangjian.agent.plugin.kafka.bean;

import java.util.Map;

public class KafkaConsumeRT {
	private Map<String, Integer> topicNum;// 拉取的每个topic的条数
	private long startTime;// 数据拉取结束时间毫秒数
	
	public Map<String, Integer> getTopicNum() {
		return topicNum;
	}
	
	public void setTopicNum(Map<String, Integer> topicNum) {
		this.topicNum = topicNum;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
}
