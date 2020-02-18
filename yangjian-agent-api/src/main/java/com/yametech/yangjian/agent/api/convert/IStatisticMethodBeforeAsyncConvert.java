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

import java.lang.reflect.Method;
import java.util.List;

/**
 * 注意：实现类不能同时实现SPI接口
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年12月21日 下午10:05:13
 */
public interface IStatisticMethodBeforeAsyncConvert extends IAsyncConvert, IConvertMatcher {
	
	/**
	 * 实例方法在调用前使用参数同步转换为临时对象
	 * @param allArguments	所有参数
	 * @param method	方法定义
	 * @return
	 */
	List<Object> convert(Object[] allArguments, Method method) throws Throwable;
	
}
