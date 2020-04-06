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
package com.yametech.yangjian.agent.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Util {
	
	/**
	 * 获取类加载器及所有上级类加载器
	 * @param cls
	 * @return
	 */
	public static List<ClassLoader> listClassLoaders(Class<?> cls) {
		if(cls == null) {
			return null;
		}
		return listClassLoaders(cls.getClassLoader());
	}
	public static List<ClassLoader> listClassLoaders(ClassLoader loader) {
		if(loader == null) {
			return null;
		}
		List<ClassLoader> classLoaders = new ArrayList<>();
		while(true) {
			if(loader == null) {
				break;
			}
			classLoaders.add(loader);
			try {
				loader = loader.getParent();
			} catch (SecurityException e) {}
		}
		return classLoaders;
	}
	
	/**
	 * 数字集合连接字符串
	 * @param delimiter
	 * @param elements
	 * @return
	 */
	public static String join(String delimiter, Iterable<?> elements) {
		if(delimiter == null || elements == null) {
			return null;
		}
		StringBuilder str = new StringBuilder();
		for(Object o : elements) {
			str.append(o).append(delimiter);
		}
		if(str.length() > 0) {
			str.delete(str.length() - delimiter.length(), str.length());
		}
		return str.toString();
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
			if(args.length > index && args[index] instanceof Class) {
				return (Class<?>) args[index];
			}
		}
		throw new RuntimeException("无法获取泛型类型");
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
			throw new RuntimeException("无法获取泛型类型");
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
				return (Class<?>) parameterized.getActualTypeArguments()[index];
			}
		}
		throw new RuntimeException("无法获取泛型类型");
	}
	
}
