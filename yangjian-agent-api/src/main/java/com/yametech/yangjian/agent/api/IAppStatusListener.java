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
package com.yametech.yangjian.agent.api;

import java.time.Duration;

import com.yametech.yangjian.agent.api.base.SPI;

public interface IAppStatusListener extends SPI {
	
	/**
	 * 配置加载完成之后执行逻辑之前时执行
	 */
	void beforeRun();

	/**
	 * 应用关闭时执行
	 * @param duration	最大等待时间
	 * @return	是否成功关闭
	 */
	boolean shutdown(Duration duration);
	
}
