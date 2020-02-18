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

package com.yametech.yangjian.agent.api.configmatch;

import java.util.regex.Pattern;

import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.bean.MethodDefined;

/**
 * 方法定义正则匹配
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月7日 下午6:12:45
 */
public class MethodRegexMatch implements IConfigMatch {
	private Pattern pattern;
	
	public MethodRegexMatch(String methodRegex) {
		pattern = Pattern.compile(methodRegex);
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		return methodDefined.getMethodDes() != null && pattern.matcher(methodDefined.getMethodDes()).matches();
	}
	
	@Override
	public String toString() {
		return pattern.pattern();
	}
	
}
