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

import java.util.List;

import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.bean.MethodDefined;

/**
 * 组合And匹配，全部匹配
 * 
 * @author liuzhao
 */
public class CombineAndMatch implements IConfigMatch {
	private List<IConfigMatch> matches;
	
	public CombineAndMatch(List<IConfigMatch> matches) {
		this.matches = matches;
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		boolean notMatch = matches.stream().anyMatch(match -> !match.isMatch(methodDefined));
		return !notMatch;
	}
	
	@Override
	public String toString() {
		StringBuilder build = new StringBuilder(" AND(");
		matches.forEach(match -> build.append(match.toString()).append('\t'));
		build.append(')');
		return build.toString();
	}
	
}
