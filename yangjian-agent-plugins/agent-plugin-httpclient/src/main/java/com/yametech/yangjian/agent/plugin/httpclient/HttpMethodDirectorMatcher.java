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

package com.yametech.yangjian.agent.plugin.httpclient;

import java.util.Arrays;

import com.yametech.yangjian.agent.api.IMetricMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.configmatch.ClassMatch;
import com.yametech.yangjian.agent.api.configmatch.CombineAndMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodArgumentNumMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodNameMatch;

/**
 * 转换httpclient调用事件
 * 支持版本：httpclient-3、httpclient-3.1
 *
 * @author dengliming
 * @date 2019/11/21
 */
public class HttpMethodDirectorMatcher implements IMetricMatcher {
    @Override
    public IConfigMatch match() {
        // 3.x
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("org.apache.commons.httpclient.HttpMethodDirector"),
                new MethodNameMatch("executeMethod"),
                new MethodArgumentNumMatch(1)));
    }

    @Override
    public String type() {
        return Constants.EventType.HTTP_CLIENT;
    }
    
    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("com.yametech.yangjian.agent.plugin.httpclient.HttpMethodDirectorConvert");
    }
    
}
