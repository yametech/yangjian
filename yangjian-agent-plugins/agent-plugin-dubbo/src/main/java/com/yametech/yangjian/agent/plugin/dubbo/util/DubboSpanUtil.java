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
package com.yametech.yangjian.agent.plugin.dubbo.util;

import brave.Span;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.trace.custom.IDubboCustom;

import java.util.Map;

/**
 * @author dengliming
 * @date 2020/8/10
 */
public class DubboSpanUtil {

    private static final String GROUP_PARAM_PREFIX = "group=";

    /**
     * 获取span名称
     *
     * @param className
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static String getSpanName(String className, String methodName, Class<?>[] parameterTypes) {
        StringBuilder name = new StringBuilder();
        name.append(className)
                .append('.').append(methodName)
                .append('(');
        for (Class<?> classes : parameterTypes) {
            name.append(classes.getSimpleName() + ",");
        }
        if (parameterTypes.length > 0) {
            name.delete(name.length() - 1, name.length());
        }
        name.append(")");
        return name.toString();
    }

    /**
     * 设置tags
     *
     * @param span
     * @param arguments
     */
    public static void setArgumentTags(Span span, Object[] arguments, IDubboCustom custom) {
        if (custom == null) {
            return;
        }
        Map<String, String> tags = custom.tags(arguments);
        if (tags != null) {
            for (Map.Entry<String, String> entry : tags.entrySet()) {
                if (StringUtil.isEmpty(entry.getKey()) || StringUtil.isEmpty(entry.getValue())) {
                    continue;
                }
                span.tag(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 通过refer参数获取dubbo的分组
     *
     * @param refer
     * @return
     */
    public static String getDubboGroup(String refer) {
        int startIdx = refer.indexOf(GROUP_PARAM_PREFIX);
        if (startIdx == -1) {
            return null;
        }
        int endIdx = refer.indexOf("&", startIdx);
        String group;
        if (endIdx == -1) {
            group = refer.substring(startIdx + GROUP_PARAM_PREFIX.length());
        } else {
            group = refer.substring(startIdx + GROUP_PARAM_PREFIX.length(), endIdx);
        }
        return group;
    }
}
