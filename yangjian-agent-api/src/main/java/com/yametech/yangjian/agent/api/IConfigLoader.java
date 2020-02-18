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

package com.yametech.yangjian.agent.api;

import com.yametech.yangjian.agent.api.base.IWeight;
import com.yametech.yangjian.agent.api.base.SPI;

/**
 * 用于加载配置的接口，目前实现包含本地配置加载、远程配置加载，实例使用默认类加载器加载
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年12月20日 上午11:15:49
 */
public interface IConfigLoader extends IWeight, SPI {
	
	void load(String arguments) throws Exception;
	
}
