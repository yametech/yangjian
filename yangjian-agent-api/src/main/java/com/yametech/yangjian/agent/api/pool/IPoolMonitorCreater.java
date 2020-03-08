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

package com.yametech.yangjian.agent.api.pool;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 
 * @Description 实现类用于完成池监控实例创建
 * 
 */
public interface IPoolMonitorCreater {
	
	/**
	 * 
	 * @param thisObj	   拦截的类实例，如果拦截的是静态方法，该值为null
	 * @param allArguments	所有的方法参数
	 * @param method	拦截的方法，如果拦截的是构造方法，该值为null
	 * @param ret	拦截的方法返回值，如果拦截的是构造方法，该值为null
	 * @param t	方法抛出的异常，没有则为null
	 * @param globalVar	其他interceptor中设置的数据
	 * @return	需监控的池对象，返回null时不监控，返回之前已生成的对象时，不会重复添加
	 */
	IPoolMonitor create(Object thisObj, Object[] allArguments, Method method,
            Object ret, Throwable t, Map<Class<?>, Object> globalVar);
}
