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
package com.yametech.yangjian.agent.core.core.elementmatch;

import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import net.bytebuddy.description.type.TypeDescription;

import java.util.List;

/**
 * 使用正则匹配类包（不含方法定义）
 *
 * 匹配内容示例：
 *  com.liuzz.myproject.app.AgentInterceptorTimed
 *
 */
public class ClassElementMatcher extends BaseElementMatcher<TypeDescription> {

    public ClassElementMatcher(IConfigMatch match, String matchType) {
        super(match, matchType);
    }

    @Override
    public List<MethodDefined> name(TypeDescription typeDescription) {
        return ElementMatcherConvert.convert(typeDescription).getMethods();
    }

}
