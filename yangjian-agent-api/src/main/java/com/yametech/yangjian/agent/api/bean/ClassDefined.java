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
package com.yametech.yangjian.agent.api.bean;

import java.util.Set;

public class ClassDefined {
	private Set<String> interfaces;// 接口
	private Set<String> parents;// 父类及祖类
	private Set<String> classAnnotations;// 类注解
	private String className;// 方法类名

	public ClassDefined(Set<String> interfaces, Set<String> parents, Set<String> classAnnotations, String className) {
		this.interfaces = interfaces;
		this.parents = parents;
		this.classAnnotations = classAnnotations;
		this.className = className;
	}

	public Set<String> getInterfaces() {
		return interfaces;
	}

	public Set<String> getParents() {
		return parents;
	}

	public Set<String> getClassAnnotations() {
		return classAnnotations;
	}
	
	public String getClassName() {
		return className;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(className);
		if(parents != null && !parents.isEmpty()) {
			builder.append(" extends ");
			parents.forEach(cls -> builder.append(cls).append(','));
			builder.deleteCharAt(builder.length() - 1);
		}
		if(interfaces != null && !interfaces.isEmpty()) {
			builder.append(" implements ");
			interfaces.forEach(cls -> builder.append(cls).append(','));
			builder.deleteCharAt(builder.length() - 1);
		}
		if(classAnnotations != null && !classAnnotations.isEmpty()) {
			builder.append(" annotations ");
			classAnnotations.forEach(cls -> builder.append(cls).append(','));
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}
	
}
