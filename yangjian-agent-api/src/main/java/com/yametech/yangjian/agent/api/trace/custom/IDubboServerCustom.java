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

import com.yametech.yangjian.agent.api.trace.ISpanCustom;

/**
 * 
 * 用于定制链路Span(是否生成Span、tag)，并通过SPI的方式加载接口实现类（SPI文件的路径即为该接口的路径）
 * dubbo服务端链路Span定制
 * @author liuzhao
 */
public interface IDubboServerCustom extends ISpanCustom, IDubboCustom {
	
}
