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
package com.yametech.yangjian.agent.util;

import com.yametech.yangjian.agent.api.common.StringUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Utils {

	private static final String URL_PREFIX = "jdbc:mysql:";

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

	public static String getStackTrace(Throwable ex) {
		StringWriter buf = new StringWriter();
		ex.printStackTrace(new PrintWriter(buf));
		return buf.toString();
	}

	/**
	 * 解析JDBC连接URL
	 *
	 * jdbc:mysql://host1:port1/database[?k1=v1[&k2=v2]...] returns "host1:port1/database"
	 * jdbc:mysql:loadbalance://host1:port1,host2:port2/database[?k1=v1[&k2=v2]...] returns host1:port1,host2:port2/database
	 * @param jdbcUrl
	 * @return
	 */
	public static String parseJdbcUrl(String jdbcUrl) {
		if (StringUtil.isEmpty(jdbcUrl)) {
			return jdbcUrl;
		}
		if (!jdbcUrl.startsWith(URL_PREFIX)) {
			return jdbcUrl;
		}

		int begin = jdbcUrl.indexOf("//");
		if (begin < 0) {
			return jdbcUrl;
		}
		int end = jdbcUrl.indexOf("?");
		if (end > 0 && begin < end) {
			return jdbcUrl.substring(begin + 2, jdbcUrl.indexOf("?"));
		}
		return jdbcUrl.substring(begin + 2);
	}
}
