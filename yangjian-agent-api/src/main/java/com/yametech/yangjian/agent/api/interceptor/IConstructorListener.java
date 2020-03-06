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

package com.yametech.yangjian.agent.api.interceptor;

/**
 * 通过实现该接口可以在构造方法调用后执行逻辑
 * 注意：实现类不能同时实现SPI接口
 * 
 * @author liuzhao
 */
public interface IConstructorListener extends IAOPConfig {

    /**
     *	构造方法调用后前执行
     * @param thisObj   增强的类实例
     * @param allArguments  所有的方法参数
     * @throws Throwable	构造方法异常
     */
	void constructor(Object thisObj, Object[] allArguments) throws Throwable;

}
