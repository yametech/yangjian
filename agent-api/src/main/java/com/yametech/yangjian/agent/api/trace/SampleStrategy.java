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
package com.yametech.yangjian.agent.api.trace;

/**
 * 采样策略
 * @author liuzhao
 */
public enum SampleStrategy {
	NONE(),
	ALWAYS(),
	FOLLOWER(),
	LEADER(),
	FOLLOWERANDLEADER();
	
	public static SampleStrategy getOrDefault(String name) {
		try {
			return valueOf(name.toUpperCase());
		} catch (Exception e) {
			return FOLLOWER;
		}
	}
	
}
