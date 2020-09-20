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
package com.yametech.yangjian.agent.api.configmatch;

import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.bean.MethodDefined;

import java.util.regex.Pattern;

/**
 * 方法名称正则匹配
 */
public class MethodNameRegexMatch implements IConfigMatch {
	private Pattern pattern;

	public MethodNameRegexMatch(String nameRegex) {
		pattern = Pattern.compile(nameRegex);
	}

	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		return methodDefined.getMethodName() != null && pattern.matcher(methodDefined.getMethodName()).matches();
	}

	@Override
	public String toString() {
		return "MethodNameRegex[" + pattern.pattern() + "]";
	}
}
