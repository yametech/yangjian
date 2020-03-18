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

public interface IContext {
	
	/**
	 * 获取上下文数据
	 * @param key	key字符串
	 * @return	设置的context数据
	 */
	Object _getAgentContext(String key);
//	Object _getAgentContext();
	
	/**
	 * 设置上下文
	 * @param key	key字符串
	 * @param value	value值
	 */
	void _setAgentContext(String key, Object value);
//	void _setAgentContext(Object value);
}
