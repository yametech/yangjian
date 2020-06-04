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
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.util.ClassUtil;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.Type;
import java.util.List;

public abstract class BaseElementMatcher<T> extends ElementMatcher.Junction.AbstractBase<T> {
	private static ILogger log = LoggerFactory.getLogger(BaseElementMatcher.class);
    private IConfigMatch match;
    private Class<?> genericType;
    private String matchType;// 日志显示需要

    private BaseElementMatcher() {
        Type type = ClassUtil.getGenericCls(this.getClass());
        if(!(type instanceof Class)) {
        	log.info("{}获取泛型失败", this.getClass());
        } else {
            genericType = (Class<?>) type;
        }
    }

    BaseElementMatcher(IConfigMatch match, String matchType) {
        this();
        this.match = match;
        this.matchType = matchType;
//        if(matches == null || matches.isEmpty()) {
//            return;
//        }
//        matchers = new ArrayList<>();
//        for(String match : matches) {
//            matchers.add(Pattern.compile(match));
//        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean matches(Object o) {
        if(match == null) {
            return false;
        }
        if(genericType == null || !(genericType.isAssignableFrom(o.getClass()))) {
            return false;
        }
//        if(o.toString().indexOf("RabbitmqConsumeAdapterAbs") != -1) {
//        	System.err.println(">>>>>");
//        }
        List<MethodDefined> matchNames;
        try {
            matchNames = name((T) o);
        } catch (Exception e) {
            String className = null;
            if(o instanceof TypeDescription) {
                className = ((TypeDescription)o).getDeclaringType().getActualName();
            }
            log.warn("转换MethodDefined异常，不执行增强：{} - {} : {}", matchType, className, e.getMessage());
            return false;
        }
        boolean isMatch = matchNames.stream().anyMatch(match::isMatch);
        if(o instanceof TypeDescription) {
        	TypeDescription type = ((TypeDescription)o);
        	StringBuilder builder = new StringBuilder();
        	for(String s : ElementMatcherConvert.getInterface(type)) {
        		builder.append(s).append(',');
        	}
//        	if(builder.toString().indexOf("IConsume") != -1) {
//        		System.err.println(">>>>>>");
//        	}
        	log.debug("{} -> {}	{}	{}", isMatch, matchType, o.toString(), builder.toString());
        } else {
        	log.debug("{} -> {}	{}", isMatch, matchType, o.toString());
        }
        return isMatch;
    }

    /**
     * 返回所有需要匹配的方法定义，针对类匹配，返回类所有的方法定义(只要有一个匹配，就返回true)，针对方法匹配，返回当前方法的定义
     * @param t
     * @return
     */
    public abstract List<MethodDefined> name(T t);
    
}
