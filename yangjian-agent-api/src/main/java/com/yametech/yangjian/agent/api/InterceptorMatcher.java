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
package com.yametech.yangjian.agent.api;

import com.yametech.yangjian.agent.api.base.IMatch;
import com.yametech.yangjian.agent.api.base.IWeight;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.base.SPI;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;

public interface InterceptorMatcher extends IMatch, IWeight, SPI {
	
	/**
	 * 返回拦截器的类路径，如:cn.ecpark.tool.javaagent.YMAgent，可包含interceptor包下任意一个接口的实现类
	 * 	因interceptor类中可能会依赖应用中的类，为了避免出现ClassNotFound，interceptor的实例使用依赖类加载的classLoader初始化，而InterceptorMatcher子类不需要，所以拆分为两个类定义
	 * @param	type 类型
	 * @param	methodDefined	拦截的方法定义
	 * @return	LoadClassKey
	 */
	LoadClassKey loadClass(MethodType type, MethodDefined methodDefined);
	
}
