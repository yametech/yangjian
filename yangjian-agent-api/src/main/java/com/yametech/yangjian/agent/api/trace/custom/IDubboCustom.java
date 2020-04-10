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
package com.yametech.yangjian.agent.api.trace.custom;

import java.util.Map;

import com.yametech.yangjian.agent.api.trace.ISpanCustom;
import com.yametech.yangjian.agent.api.trace.ISpanSample;

/**
 * 
 * 用于定制链路Span(是否生成Span、tag)，并通过SPI的方式加载接口实现类（SPI文件的路径即为该接口的路径）
 * dubbo链路Span tags定制
 * @author liuzhao
 */
public interface IDubboCustom extends ISpanCustom {
	
	/**
	 * 
	 * @param interfaceCls	接口类
	 * @param methodName	调用的方法名
	 * @param parameterTypes	方法参数类型
	 * @return	是否匹配，true：则执行sample、tags，false：不执行
	 */
	boolean filter(Class<?> interfaceCls, String methodName, Class<?>[] parameterTypes);
	
	/**
	 * 
	 * @param params	方法参数
	 * @Param configSample	配置的采样实例
	 * @return	true：采样，false：不采样
	 */
	boolean sample(Object[] params, ISpanSample configSample);
	
	/**
	 * 
	 * @param params	方法参数
	 * @return	trace tag数据
	 */
	Map<String, String> tags(Object[] params);
	
}
