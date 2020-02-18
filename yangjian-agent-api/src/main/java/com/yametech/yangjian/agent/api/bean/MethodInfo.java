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

public class MethodInfo {
	private Class<?> cls;
	private String methodName;
	private Class<?>[] parameterTypes;
	
	public MethodInfo(Class<?> cls, String methodName, Class<?>[] parameterTypes) {
		this.cls = cls;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
	}
	
	public Class<?> getCls() {
		return cls;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	
	@Override
	public int hashCode() {
		return cls.getName().hashCode() ^ methodName.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MethodInfo) {
			MethodInfo other = (MethodInfo)obj;
            if ((cls == other.getCls()) && (methodName.equals(other.getMethodName()))) {
                return equalParamTypes(parameterTypes, other.getParameterTypes());
            }
        }
        return false;
	}
	
	boolean equalParamTypes(Class<?>[] params1, Class<?>[] params2) {
        if (params1.length == params2.length) {
            for (int i = 0; i < params1.length; i++) {
                if (params1[i] != params2[i])
                    return false;
            }
            return true;
        }
        return false;
    }
	
}
