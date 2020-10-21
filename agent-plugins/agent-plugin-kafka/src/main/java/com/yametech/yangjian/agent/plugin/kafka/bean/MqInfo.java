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

public class MqInfo {
	private String ipPorts;// ip、端口，如：192.168.1.1:9092,192.168.1.1:9093
	private String topic;// 主题
	private String consumeGroup;// 消费标识，kafka为consumeGroup，rabbitmq为queue

	public MqInfo(String ipPorts, String topic, String consumeGroup) {
		this.ipPorts = ipPorts;
		this.topic = topic;
		this.consumeGroup = consumeGroup;
	}

	public String getIpPorts() {
		return ipPorts;
	}

	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getConsumeGroup() {
		return consumeGroup;
	}
	
	public void setConsumeGroup(String consumeGroup) {
		this.consumeGroup = consumeGroup;
	}
	
}
