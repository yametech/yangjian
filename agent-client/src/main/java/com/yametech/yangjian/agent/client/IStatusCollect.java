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

/**
 * 状态收集接口，agent会定时调用collect收集状态，并上报到监控后端
 * @Description 
 * 
 * @author liuzhao
 * @date 2020年4月23日 下午3:13:23
 */
public interface IStatusCollect {
	
	/**
	 * 
	 * @return	返回服务运行状态字符串，长度不能超过200，如果返回null则不上报信息
	 */
	StatusReturn collect();
	
	/**
	 * 
	 * @return	状态类型，长度不能超过20，不可为null
	 */
	String type();
	
	/**
	 * 状态，
	 * 		-1 - 关闭；
	 * 		0 - 禁用；
	 * 		1 - 启用；
	 * -1状态的对象不会再执行collect；0、1状态可以多次变更，禁用时不执行collect，启用时会定时志聪collect；
	 * @return
	 */
	default int status() {
		return 1;
	}
	
	/**
	 * collect执行间隔秒数
	 * @return	间隔秒数
	 */
	default int interval() {
		return 10;
	}
}
