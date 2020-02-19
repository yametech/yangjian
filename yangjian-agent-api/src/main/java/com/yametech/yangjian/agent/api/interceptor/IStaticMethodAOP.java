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

import java.lang.reflect.Method;
import java.util.Map;

import com.yametech.yangjian.agent.api.bean.BeforeResult;

/**
 * 通过实现该接口可以在静态方法调用前后拦截执行逻辑，目前实现类包含：发送方法调用事件到内部队列由IMethodEventListener的实现类消费
 * 注意：实现类不能同时实现SPI接口
 * 
 * @author liuzhao
 */
public interface IStaticMethodAOP<T> {

    /**
     *增强方法调用之前执行
     * @param allArguments  所有的方法参数
     * @param method    类方法定义实例
     * @return	BeforeResult 可为null
     * @throws Throwable	方法调用异常
     */
	BeforeResult<T> before(Object[] allArguments, Method method) throws Throwable;

    /**
     *增强方法调用之后执行
     * @param allArguments  所有的方法参数
     * @param method    类方法定义实例
     * @param beforeResult  before的返回值，可能为null
     * @param ret   方法实际返回值，如果before有设置返回值，该值也不一定为before中的返回值，可能后续拦截时重写了返回值
     * @param t 异常，没有则为null
     * @param globalVar value为before返回值中的globalVar，key为IMethodAOP实例class，没有则为null
     * @return	Object
     * @throws Throwable	方法调用异常
     */
	Object after(Object[] allArguments, Method method, BeforeResult<T> beforeResult, Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable;

    /**
     * 增强方法处理异常时执行
     * @param allArguments  所有的方法参数
     * @param method    类方法定义实例
     * @param beforeResult  before的返回值，可能为null
     * @param t 异常
     * @param globalVar value为before返回值中的globalVar，key为IMethodAOP实例class，没有则为null
     * @throws Throwable	方法调用异常
     */
    default void exception(Object[] allArguments, Method method, BeforeResult<T> beforeResult, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {}

}
