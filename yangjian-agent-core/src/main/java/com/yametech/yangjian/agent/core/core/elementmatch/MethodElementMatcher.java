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
import net.bytebuddy.description.method.MethodDescription;

import java.util.Collections;
import java.util.List;

/**
 * 使用正则匹配类方法
 *
 * 匹配内容示例：
 *  public boolean java.lang.Object.equals(java.lang.Object)
 *  public native int java.lang.Object.hashCode()
 *  protected native java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
 *  public void com.liuzz.myproject.app.AgentInterceptorTimed.helloSleep(java.lang.String) throws java.lang.InterruptedException
 *
 */
public class MethodElementMatcher extends BaseElementMatcher<MethodDescription> {
    public MethodElementMatcher(IConfigMatch match, String matchType) {
        super(match, matchType);
    }

    @Override
    public List<MethodDefined> name(MethodDescription methodDescription) {
        return Collections.singletonList(ElementMatcherConvert.convert(methodDescription.asDefined()));
    }
    
}
