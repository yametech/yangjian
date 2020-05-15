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
package com.yametech.yangjian.agent.api.base;

import com.yametech.yangjian.agent.api.bean.MethodDefined;

public interface IMatch {
	
	/**
	 * @return	匹配配置
	 */
	IConfigMatch match();
	
	/**
	 * match匹配的方法会回调该方法
	 * @param methodDefined	方法定义完整路径
	 */
	default void method(MethodDefined methodDefined) {}

	/**
	 * 类增强成功后通知
	 * @param typeName	增强的类
	 * @param classLoader	增强类使用的classLoader
	 * @param loaded	是否已加载类
	 */
	default void onComplete(String typeName, ClassLoader classLoader, boolean loaded) throws Exception {}

	/**
	 * 类增强失败时通知
	 * @param typeName	增强的类
	 * @param classLoader	增强类使用的classLoader
	 * @param loaded	是否已加载类
	 * @param throwable	增强失败时的异常
	 */
	default void onError(String typeName, ClassLoader classLoader, boolean loaded, Throwable throwable) {}


}
