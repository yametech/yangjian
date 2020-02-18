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

package com.yametech.yangjian.agent.plugin.dubbo;

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
}
