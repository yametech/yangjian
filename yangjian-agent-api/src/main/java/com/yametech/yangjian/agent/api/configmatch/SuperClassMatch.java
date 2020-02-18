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

import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.bean.MethodDefined;

/**
 * 继承类匹配（可以是父类或间接父类）
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月7日 下午6:12:26
 */
public class SuperClassMatch implements IConfigMatch {
	private String superClass;
	
	public SuperClassMatch(String superClass) {
		this.superClass = superClass;
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		return methodDefined.getClassDefined().getParents() != null && methodDefined.getClassDefined().getParents().contains(superClass);
	}
	
	@Override
	public String toString() {
		return superClass;
	}
	
}
