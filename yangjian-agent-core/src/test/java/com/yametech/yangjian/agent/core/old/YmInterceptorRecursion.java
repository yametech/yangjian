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
package com.yametech.yangjian.agent.core.old;
//package cn.ecpark.tool.javaagent.core.interceptor;
//
//import java.lang.reflect.Method;
//import java.util.concurrent.Callable;
//
//import cn.ecpark.tool.javaagent.api.IMethodAOP;
//import cn.ecpark.tool.javaagent.log.ILogger;
//import cn.ecpark.tool.javaagent.log.LoggerFactory;
//import net.bytebuddy.implementation.bind.annotation.AllArguments;
//import net.bytebuddy.implementation.bind.annotation.Origin;
//import net.bytebuddy.implementation.bind.annotation.RuntimeType;
//import net.bytebuddy.implementation.bind.annotation.SuperCall;
//import net.bytebuddy.implementation.bind.annotation.This;
//
///**
// * 测试使用，与YmInterceptor对比，该方法性能稍差
// * @Description 
// * 
// * @author liuzhao
// * @date 2019年11月5日 上午9:24:02
// */
//@Deprecated
//public class YmInterceptorRecursion {
//	private static final ILogger log = LoggerFactory.getLogger(YmInterceptorOne.class);
//    private IMethodAOP<?>[] interceptors;
//
//    public YmInterceptorRecursion(IMethodAOP<?>[] interceptors) {
//        this.interceptors = interceptors;
//    }
//
//    @RuntimeType
//    public Object intercept(@This Object thisObj, @AllArguments Object[] allArguments,
//                                   @SuperCall Callable<?> callable, @Origin Method method) throws Throwable {
//        if(interceptors == null || interceptors.length == 0) {
//            return callable.call();
//        }
//        return call(interceptors, 0, thisObj, allArguments, method, callable, null);
//    }
//
//    /**
//     * 通过递归的方式执行（使用栈简化中间变量的使用）
//     * @param methodInterceptors    所有匹配的过滤器，包含启用和未启用的
//     * @param index 需执行的过滤器位置
//     * @param thisObj   当前实例名称
//     * @param allArguments  方法参数
//     * @param method    方法
//     * @param callable  调用器
//     * @param ret   返回值
//     * @return  方法返回值
//     * @throws Throwable
//     *  仅抛出方法抛出的异常，拦截器的异常会屏蔽
//     */
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//	private Object call(IMethodAOP<?>[] methodInterceptors, int index,
//                               Object thisObj, Object[] allArguments, Method method,
//                               Callable<?> callable, Object ret) throws Throwable {
//        if(index >= methodInterceptors.length) {
//            return ret == null ? callable.call() : ret;
//        }
//        IMethodAOP<?> methodInterceptor = methodInterceptors[index];
//        boolean enable = methodInterceptor.enable();
//        if(!enable) {
//            return call(methodInterceptors, ++index, thisObj, allArguments, method, callable, ret);
//        }
//
//        BeforeResult result;
//        try {
//            result = methodInterceptor.before(thisObj, allArguments, method);
//            if(result != null && result.getRet() != null ) {
//                ret = result.getRet();
//            }
//        } catch (Throwable t) {
//        	log.warn(t, "interceptor before");
//            // before异常，不再执行after和exception
//            return call(methodInterceptors, ++index, thisObj, allArguments, method, callable, ret);
//        }
//
//        Throwable methodThrowable = null;
//        try {
//            ret = call(methodInterceptors, ++index, thisObj, allArguments, method, callable, ret);
//        } catch (Throwable t) {
//        	log.warn(t, "interceptor call");
//            methodThrowable = t;
//        }
//
//        try {
//            if(methodThrowable != null) {
//                // exception处理异常，不再执行after
//                methodInterceptor.exception(thisObj, allArguments, method, result, methodThrowable);
//            }
//            ret = methodInterceptor.after(thisObj, allArguments, method, result, ret, methodThrowable);
//        } catch (Throwable t) {
//        	log.warn(t, "interceptor exception/after");
//        }
//
//        if(methodThrowable == null) {
//            return ret;
//        } else {
//            throw methodThrowable;
//        }
//    }
//
//}
