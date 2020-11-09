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
import com.yametech.yangjian.agent.api.common.Constants;
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
     * 获取dubbo泛化调用实际的方法名
     *
     * @param interfaceName 实际的接口名
     * @param args 对应org.apache.dubbo.rpc.service.GenericService.$invoke的args参数
     * @return
     */
    public static String getGenericInterfaceName(String interfaceName, Object[] args) {
        if (StringUtil.isEmpty(interfaceName) || args.length < 2) {
            return null;
        }

        try {
            StringBuilder sb = new StringBuilder();
            String methodName = args[0].toString();
            String[] parameterTypes = (String[]) args[1];
            sb.append(interfaceName).append(".").append(methodName).append("(");
            for (int i = 0; i < parameterTypes.length; i++) {
                sb.append(parameterTypes[i].substring(parameterTypes[i].lastIndexOf(".") + 1));
                if (i < (parameterTypes.length - 1)) {
                    sb.append(',');
                }
            }
            return sb.append(")").toString();
        } catch (Throwable t) {
            //
        }
        return null;
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
        if (refer == null) {
            return null;
        }
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

    /**
     * 设置调用方的服务名和方法标识（用于采集服务调用关联关系）
     *
     * @param span
     * @param parentServiceName
     * @param agentSign
     * @param interfaceName
     */
    public static void tagAgentSign(Span span, String parentServiceName, String agentSign, String interfaceName) {
        if (StringUtil.isEmpty(span.context().parentIdString()) || StringUtil.isEmpty(parentServiceName)
                || StringUtil.isEmpty(agentSign)) {
            return;
        }
        if (verifyAgentSign(agentSign, interfaceName)) {
            span.tag(Constants.Tags.PARENT_SERVICE_NAME, parentServiceName);
            span.tag(Constants.Tags.AGENT_SIGN, agentSign);
        }
    }

    /**
     * 校验接口名是否一致
     *
     * @param agentSign
     * @param interfaceName
     * @return
     */
    private static boolean verifyAgentSign(String agentSign, String interfaceName) {
        int endIndex = agentSign.lastIndexOf(".");
        if (endIndex == -1) {
            return false;
        }
        int startIndex = 0;
        // 含有分组标识
        int groupIndex = agentSign.indexOf("/");
        if (groupIndex != -1) {
            startIndex = groupIndex + 1;
        }

        agentSign = agentSign.substring(startIndex, endIndex);
        return interfaceName.equals(agentSign);
    }
}
