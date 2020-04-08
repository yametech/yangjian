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
package com.yametech.yangjian.agent.plugin.dubbo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Utils {
	/**
	 * 检测参数是否合法，非法时抛出异常
	 * @param expression	为true时抛出异常
	 * @param errorMessageTemplate
	 * @param errorMessageArgs
	 */
	public static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
		if (expression) {
			throw new IllegalArgumentException(String.format(errorMessageTemplate, errorMessageArgs));
		}
	}
	
	/**
	 * 非法状态检测
	 * @param expression	为true时抛出异常
	 * @param errorMessageTemplate
	 * @param errorMessageArgs
	 */
	public static void checkStatus(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
		if (expression) {
			throw new IllegalStateException(String.format(errorMessageTemplate, errorMessageArgs));
		}
	}
	
	/**
	 * 字符串首字母小写
	 * @param s
	 * @return
	 */
	public static String toLowerCaseFirstChar(String s) {
		if(s == null || s.trim().equals("")) {
			return s;
		}
		if (Character.isLowerCase(s.charAt(0))) {
			return s;
		} else {
			return new StringBuilder().append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
		}
	}
	
	/**
	 * 获取父类泛型类型
	 * @param clazz	类类型
	 * @param index	泛型index
	 * @return
	 */
	public static Class<?> superClassGeneric(Class<?> clazz, int index) {
		Type t = clazz.getGenericSuperclass();
		if (t instanceof ParameterizedType) {
			Type[] args = ((ParameterizedType) t).getActualTypeArguments();
			if(args.length > index) {
				if(args[index] instanceof Class) {
					return (Class<?>) args[index];
				} else if(args[index] instanceof ParameterizedType) {
					return (Class<?>) ((ParameterizedType)args[index]).getRawType();
				}
			}
		}
		throw new RuntimeException("无法获取泛型类型：" + clazz + "	" + index);
	}
	
	/**
	 * 获取接口泛型类型
	 * @param clazz	类类型
	 * @param inter	接口类型
	 * @param index	泛型index
	 * @return
	 */
	public static Class<?> interfacesGeneric(Class<?> clazz, Class<?> inter, int index) {
		Type[] types = clazz.getGenericInterfaces();
		if(types == null) {
			throw new RuntimeException("无法获取泛型类型：" + clazz + "	" + inter + "	" + index);
		}
		for(Type type : types) {
			if(!(type instanceof ParameterizedType)) {
				continue;
			}
			ParameterizedType parameterized = (ParameterizedType) type;
			if(!parameterized.getRawType().getTypeName().equals(inter.getName())) {
				continue;
			}
			if(parameterized.getActualTypeArguments() != null && parameterized.getActualTypeArguments().length > index) {
				Type actualType = parameterized.getActualTypeArguments()[index];
				if(actualType instanceof Class) {
					return (Class<?>) parameterized.getActualTypeArguments()[index];
				} else if(actualType instanceof ParameterizedType) {
					return (Class<?>) ((ParameterizedType)actualType).getRawType();
				}
			}
		}
		throw new RuntimeException("无法获取泛型类型：" + clazz + "	" + inter + "	" + index);
	}
}
