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

import java.util.List;

import com.yametech.yangjian.agent.api.bean.TimeEvent;

/**
 * 注意：实现类不能同时实现SPI接口
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年12月21日 下午10:05:13
 */
public interface IAsyncConvert {
	/**
	 * 异步执行临时对象转换为 TimeEvent，主要用于直接转换为TimeEvent较耗时的场景，将转换步骤分为耗时较少的convert与耗时较多的convert
	 * 注意：此处需注意临时对象不包含可变数据，防止异步执行时内部数据变更导致转换失败
	 * @param eventBean
	 * @return
	 */
	List<TimeEvent> convert(Object eventBean);
	
}
