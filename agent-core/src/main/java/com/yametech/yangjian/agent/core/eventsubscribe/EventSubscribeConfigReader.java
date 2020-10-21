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
package com.yametech.yangjian.agent.core.eventsubscribe;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.SPI;
import com.yametech.yangjian.agent.api.common.InstanceManage;
import com.yametech.yangjian.agent.api.configmatch.ClassMatch;
import com.yametech.yangjian.agent.api.configmatch.CombineAndMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodConstructorMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodRegexMatch;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.eventsubscribe.event.EventMatcher;
import com.yametech.yangjian.agent.core.eventsubscribe.subscribe.SubscribeMatcher;

import java.util.*;

/**
 * 
 * @Description 基于配置初始化EventMatcher
 * 
 * @author liuzhao
 * @date 2020年4月21日 下午3:14:22
 */
public class EventSubscribeConfigReader implements IConfigReader, SPI {
	private static final ILogger LOG = LoggerFactory.getLogger(EventSubscribeConfigReader.class);
	private volatile boolean init = false;
	private static final String EVENTGROUP_PREFIX = "eventSubscribe.group.";
	
	@Override
    public Set<String> configKey() {
        return new HashSet<>(Collections.singletonList(EVENTGROUP_PREFIX.replaceAll("\\.", "\\\\.") + ".*"));
    }

    /**
     * 	覆盖更新
     *
     * @param kv 配置数据
     */
    @Override
    public synchronized void configKeyValue(Map<String, String> kv) {
    	if(init) {
    		return;
    	}
    	init = true;
        if (kv == null) {
            return;
        }
        kv.entrySet().stream().filter(config -> config.getKey().startsWith(EVENTGROUP_PREFIX)).forEach(entry -> {
        	String eventGroup = entry.getKey();
        	String[] eventInfo = entry.getValue().split(">", 2);
        	if(eventInfo.length != 2) {
        		LOG.warn("config error: {} = {}", entry.getKey(), entry.getValue());
        		return;
        	}
        	String source = eventInfo[0].trim();
        	String target = eventInfo[1].trim();
        	InstanceManage.registry(new EventMatcher(eventGroup, new MethodRegexMatch(source)));
        	String className = getClass(target);
        	if(className == null) {
        		LOG.warn("match rule config error: {} - {}，must contains class define", eventGroup, target);
        		return;
        	}
        	InstanceManage.registry(new SubscribeMatcher(eventGroup,
        			new CombineAndMatch(Arrays.asList(new ClassMatch(className), new MethodConstructorMatch())), getMatch(target)));
        	LOG.info("load event config: {} = {}", entry.getKey(), entry.getValue());
        });
    }
    
    private static IConfigMatch getMatch(String source) {
    	String className = getClass(source);
    	if(className == null) {
    		return new MethodRegexMatch(".*" + source);
    	}
    	return new MethodRegexMatch(".*" + source.replaceFirst(className, className.replaceAll("\\.", "\\\\\\\\.") + "\\\\"));
    }
    
    private static String getClass(String source) {
    	int index = source.indexOf('(');
    	if(index == -1) {
    		int endIndex = source.lastIndexOf('.');
    		if(endIndex == -1) {
    			return null;
    		}
    		return source.substring(0, endIndex);
    	}
    	int pointIndex = source.substring(0, index).lastIndexOf('.');
    	if(pointIndex == -1) {
			return null;
		}
    	return source.substring(0, pointIndex);
    }
    
}
