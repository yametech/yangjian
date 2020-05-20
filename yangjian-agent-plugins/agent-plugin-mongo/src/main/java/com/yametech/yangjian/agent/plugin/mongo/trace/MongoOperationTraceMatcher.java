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
package com.yametech.yangjian.agent.plugin.mongo.trace;

import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.configmatch.ClassMatch;
import com.yametech.yangjian.agent.api.configmatch.CombineAndMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodNameMatch;
import com.yametech.yangjian.agent.api.trace.ITraceMatcher;
import com.yametech.yangjian.agent.api.trace.TraceType;

import java.util.Arrays;

/**
 * mongo集合操作方法拦截
 *
 * 支持版本：3.0.x~3.5.x
 * com.mongodb.Mongo#execute(...)
 *
 * @author dengliming
 * @date 2020/5/18
 */
public class MongoOperationTraceMatcher implements ITraceMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("com.mongodb.Mongo"),
                new MethodNameMatch("execute")
        ));
    }

    @Override
    public TraceType type() {
        return TraceType.MONGO;
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
    	return new LoadClassKey("com.yametech.yangjian.agent.plugin.mongo.trace.OperationExecutorSpanCreater");
    }
    
}
