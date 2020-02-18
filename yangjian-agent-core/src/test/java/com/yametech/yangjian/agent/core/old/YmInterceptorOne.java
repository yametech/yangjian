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
//
///**
// * 测试使用
// * @Description 
// * 
// * @author liuzhao
// * @date 2019年11月5日 上午9:24:02
// */
//@Deprecated
//public class YmInterceptorOne {
//	private static final ILogger log = LoggerFactory.getLogger(YmInterceptorOne.class);
//    private IMethodAOP<?> interceptor;
//
//    public YmInterceptorOne(IMethodAOP<?> interceptor) {
//        this.interceptor = interceptor;
//    }
//
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//	@RuntimeType
//    public Object intercept(Object thisObj, @AllArguments Object[] allArguments,
//                                   @SuperCall Callable<?> callable, @Origin Method method) throws Throwable {
//        if(interceptor == null || !interceptor.enable()) {
//            return callable.call();
//        }
//        boolean skipAfter = false;
//        BeforeResult result = null;
//        try {
//            result = interceptor.before(thisObj, allArguments, method);
//        } catch (Throwable t) {
//        	log.warn(t, "interceptor before");
//            // before异常，不再执行after和exception
//            skipAfter = true;
//        }
//
//        Object ret = null;
//        Throwable methodThrowable = null;
//        if (result != null && result.getRet() != null) {
//            ret = result.getRet();
//        } else {
//            // 此处需修改，在多个拦截器时，如果前面的拦截器重写了返回值，会导致后面的拦截器不在执行
//            try {
//                ret = callable.call();
//            } catch (Throwable t) {
//            	log.warn(t, "interceptor call");
//                methodThrowable = t;
//            }
//        }
//
//        if(!skipAfter) {
//            try {
//                if(methodThrowable != null) {
//                    // exception处理异常，不再执行after
//                    interceptor.exception(thisObj, allArguments, method, result, methodThrowable);
//                }
//                ret = interceptor.after(thisObj, allArguments, method, result, ret, methodThrowable);
//            } catch (Throwable t) {
//            	log.warn(t, "interceptor exception/after");
//            }
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
