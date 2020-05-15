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
package com.yametech.yangjian.agent.core.core.agent;

import com.yametech.yangjian.agent.api.bean.Annotation;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.core.elementmatch.ElementMatcherConvert;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

import java.util.Set;

public class AgentListener implements AgentBuilder.Listener {
	private static ILogger log = LoggerFactory.getLogger(AgentListener.class);
	
    @Override
    public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule javaModule, boolean loaded) {

    }

    @Override
    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b, DynamicType dynamicType) {
    	Set<String> superClasses = ElementMatcherConvert.getSuperClass(typeDescription);
    	Set<String> interfaces = ElementMatcherConvert.getInterface(typeDescription);
    	Set<Annotation> annotations = ElementMatcherConvert.getClassAnnotation(typeDescription);
    	StringBuilder builder = new StringBuilder();
    	if(!superClasses.isEmpty()) {
    		builder.append(" extends ");
    		superClasses.forEach(cls -> builder.append(cls).append(','));
    		builder.deleteCharAt(builder.length() - 1);
    	}
    	if(!interfaces.isEmpty()) {
    		builder.append(" implements ");
    		interfaces.forEach(cls -> builder.append(cls).append(','));
    		builder.deleteCharAt(builder.length() - 1);
    	}
    	if(annotations != null && !annotations.isEmpty()) {
    		builder.append(" annotations ");
    		annotations.forEach(cls -> builder.append(cls).append(','));
    		builder.deleteCharAt(builder.length() - 1);
    	}
    	
    	log.info("Transformation:{} -> {}", typeDescription.getActualName(), builder.toString());
    }

    @Override
    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean loaded) {

    }

    @Override
    public void onError(String typeName, ClassLoader classLoader, JavaModule javaModule, boolean loaded, Throwable throwable) {
		log.warn(throwable, "Enhance class {} error.", typeName);
		try {
			EnhanceListener.notifyAndUnregister(typeName, classLoader, loaded, throwable);
		} catch (Throwable e) {
			log.warn("enhance error notify exception:{}	{}", typeName, classLoader, e);
		}
    }

    @Override
    public void onComplete(String typeName, ClassLoader classLoader, JavaModule javaModule, boolean loaded) {
    	try {
			EnhanceListener.notifyAndUnregister(typeName, classLoader, loaded, null);
    	} catch (Throwable e) {
			log.warn("enhance complete notify exception:{}	{}", typeName, classLoader, e);
		}
    }
}
