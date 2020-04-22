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
package com.yametech.yangjian.agent.plugin.kafka.context;

import java.util.Arrays;

import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.configmatch.ClassMatch;
import com.yametech.yangjian.agent.api.configmatch.CombineAndMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodArgumentNumMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodNameMatch;

/**
 * 增强类定义
 *
 * @author liuzhao
 * @Description
 * @date 2019年11月8日 下午6:13:04
 */
public class ConsumeTimeMatcher implements InterceptorMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("org.apache.kafka.clients.consumer.KafkaConsumer"),
                new MethodNameMatch("poll"),
                new MethodArgumentNumMatch(2)
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
    	return new LoadClassKey("com.yametech.yangjian.agent.plugin.kafka.context.ConsumeTimeInterceptor");
    }
    
    @Override
    public int weight() {
        return 99;
    }
}
