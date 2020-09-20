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
package com.yametech.yangjian.agent.plugin.spring.trace;

import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.configmatch.*;
import com.yametech.yangjian.agent.api.trace.ITraceMatcher;
import com.yametech.yangjian.agent.api.trace.TraceType;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2020/4/20
 */
public class ControllerTraceMatcher implements ITraceMatcher {

    @Override
    public TraceType type() {
        return TraceType.HTTP_SERVER;
    }

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new CombineOrMatch(Arrays.asList(
                        new ClassAnnotationMatch("org.springframework.stereotype.Controller"),
                        new ClassAnnotationMatch("org.springframework.web.bind.annotation.RestController")
                )),
                new CombineOrMatch(Arrays.asList(
                        new MethodAnnotationMatch("org.springframework.web.bind.annotation.RequestMapping"),
                        new MethodAnnotationMatch("org.springframework.web.bind.annotation.GetMapping"),
                        new MethodAnnotationMatch("org.springframework.web.bind.annotation.PostMapping"),
                        new MethodAnnotationMatch("org.springframework.web.bind.annotation.PutMapping"),
                        new MethodAnnotationMatch("org.springframework.web.bind.annotation.DeleteMapping"),
                        new MethodAnnotationMatch("org.springframework.web.bind.annotation.PatchMapping")))
        ));
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
        return new LoadClassKey("com.yametech.yangjian.agent.plugin.spring.trace.ControllerSpanCreater");
    }
}
