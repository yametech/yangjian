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

package com.yametech.yangjian.agent.api.base;

import com.yametech.yangjian.agent.api.bean.MethodDefined;

public interface IMatch {
	
	/**
	 * 返回匹配配置
	 * @return
	 */
	IConfigMatch match();
	
	/**
	 * match匹配的方法会回调该方法
	 * @param methodInfo	方法定义完整路径
	 */
//	void method(String methodInfo);
	default void method(MethodDefined methodDefined) {}
	
	/**
	 * match匹配的方法会回调该方法
	 * @param className	类完整路径
	 * @param methodName	方法名
	 * @param agruments	参数
	 */
//	void method(String className, String methodName, String[] agruments);
}
