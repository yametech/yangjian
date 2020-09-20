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
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
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
	
	/**
	 * 获取本机IP（获取本地所有IP地址）
	 *
	 * @param includeStart	包含的前缀匹配，可为null
	 * @param excludeStart	排除的前缀匹配，可为null
	 * @return
	 */
	public static String getIpAddress(String[] includeStart, String[] excludeStart) {
		StringBuilder sb = new StringBuilder();
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			List<String> ipList = new ArrayList<>();
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = allNetInterfaces.nextElement();
				// 过滤 127.0.0.1和非活跃网卡
				if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
					continue;
				}
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = addresses.nextElement();
					if (!(ip instanceof Inet4Address)) {
						continue;
					}
					ipList.add(ip.getHostAddress());
				}
			}

			ipList.stream().filter(ipAddress -> {
				if (excludeStart != null) {
					for (String start : excludeStart) {
						if (ipAddress.startsWith(start)) {
							return false;
						}
					}
				}
				if (includeStart == null) {
					return true;
				}
				for (String start : includeStart) {
					if (ipAddress.startsWith(start)) {
						return true;
					}
				}
				return false;
			}).forEach(ipAddress -> sb.append(ipAddress).append(","));

			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
		} catch (Exception e) {}
		return sb.toString();
	}

}
