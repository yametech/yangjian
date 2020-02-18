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

package com.yametech.yangjian.agent.core.core.agent;

//public class MatchInfo {
//    // 访问符，为null时不限制，空字符串代表当前包可访问，如：public、protected、空字符串、private
////    private String access;
//    // 访问符结尾到方法类名开始之间的匹配，为null时不限制
////    private String betweenAccessAndClass;
//    // 需要增强的类包匹配正则，匹配内容示例：com.liuzz.myproject.app.AgentInterceptorTimed，格式为类全路径
//    private String classRegular;
//    private Pattern classPattern;
//    // 需要增强的方法匹配正则，匹配内容示例：public void com.liuzz.myproject.app.AgentInterceptorTimed.helloSleep(java.lang.String) throws java.lang.InterruptedException、public native int java.lang.Object.hashCode()，包含方法定义的所有内容
//    private String methodRegular;
//    private Pattern methodPattern;
//
//    public MatchInfo(String classRegular, String methodRegular) {
//        setClassRegular(classRegular);
//        setMethodRegular(methodRegular);
//    }
//
//    public String getClassRegular() {
//        return classRegular;
//    }
//
//    public void setClassRegular(String classRegular) {
//        this.classRegular = classRegular;
//        if(classRegular != null) {
//            classPattern = Pattern.compile(classRegular);
//        }
//    }
//
//    public String getMethodRegular() {
//        return methodRegular;
//    }
//
//    public void setMethodRegular(String methodRegular) {
//        this.methodRegular = methodRegular;
//        if(methodRegular != null) {
//            methodPattern = Pattern.compile(methodRegular);
//        }
//    }
//
//    public Pattern getClassPattern() {
//        return classPattern;
//    }
//
//    public Pattern getMethodPattern() {
//        return methodPattern;
//    }
//    
//    public boolean match(String classFullName, String methodInfo) {
//    	return classPattern != null && classPattern.matcher(classFullName).matches()
//                && methodPattern != null && methodPattern.matcher(methodInfo).matches();
//    }
//
//    //    public String getMethodMatch() {
////        StringBuilder matchBuffer = new StringBuilder();
////        if(access != null) {
////            if(access.trim().length() > 0) {
////                matchBuffer.append("^").append(access.trim()).append(" ");
////            } else {
////                matchBuffer.append("!(^public |^private |^protected )");
////            }
////        }
////        if(betweenAccessAndClass != null) {
////
////        }
////
////        matchBuffer.append(betweenAccessAndClass == null ? "*" : betweenAccessAndClass);
////        matchBuffer.append(betweenAccessAndClass == null ? "*" : betweenAccessAndClass);
////        if(classMatch != null) {
////
////        }
////        return
////    }
//}
