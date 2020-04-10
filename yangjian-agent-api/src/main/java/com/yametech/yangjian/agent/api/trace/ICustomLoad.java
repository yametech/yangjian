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

import java.util.List;

/**
 * 
 * 定制Span属性接口，此处通过泛型获取需要加载的接口实例类型，通过custom回调并带上泛型对应的实例
 * 注意：一定要指定泛型，否则无法获取接口实例，该方式可简化配置
 * @author liuzhao
 */
public interface ICustomLoad<T extends ISpanCustom> {
	
	/**
	 * 
	 * @param customs	Span定制实例
	 */
	void custom(List<T> customs);
	
}
