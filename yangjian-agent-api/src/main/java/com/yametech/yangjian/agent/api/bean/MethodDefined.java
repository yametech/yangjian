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

package com.yametech.yangjian.agent.api.bean;

import java.util.Set;

public class MethodDefined {
	private ClassDefined classDefined;// 类定义
	private String methodDes;// 方法描述
	private Set<String> methodAnnotations;// 方法注解
	private String methodName;// 方法名
	private String[] params;// 方法参数类型
	private String methodRet;// 方法返回值类型

	public MethodDefined(ClassDefined classDefined, Set<String> methodAnnotations, 
			String methodDes, String methodName, String[] params, String methodRet) {
		this.classDefined = classDefined;
		this.methodDes = methodDes;
		this.methodAnnotations = methodAnnotations;
		this.methodName = methodName;
		this.params = params;
		this.methodRet = methodRet;
	}

	public ClassDefined getClassDefined() {
		return classDefined;
	}
	
	public Set<String> getMethodAnnotations() {
		return methodAnnotations;
	}
	
	public String getMethodDes() {
		return methodDes;
	}

	public String getMethodName() {
		return methodName;
	}

	public String[] getParams() {
		return params;
	}

	public String getMethodRet() {
		return methodRet;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString()).append('\t').append(methodDes);
		if(methodAnnotations != null && !methodAnnotations.isEmpty()) {
			builder.append(" annotations ");
			methodAnnotations.forEach(cls -> builder.append(cls).append(','));
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}
	
}
