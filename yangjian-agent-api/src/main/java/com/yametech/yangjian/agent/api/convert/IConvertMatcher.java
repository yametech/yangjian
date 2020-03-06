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

package com.yametech.yangjian.agent.api.convert;

import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.interceptor.IAOPConfig;

public interface IConvertMatcher extends IAOPConfig {
	
	/**
	 * @param startTime	开始事件
	 * @return	获取默认TimeEvent
	 */
	default TimeEvent get(long startTime) {
		TimeEvent event = new TimeEvent();
		long now = System.currentTimeMillis();
		event.setEventTime(now);
        event.setUseTime(now - startTime);
        return event;
	}
}
