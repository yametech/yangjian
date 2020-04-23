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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.SPI;
import com.yametech.yangjian.agent.api.configmatch.ClassMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodRegexMatch;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.core.InstanceManage;
import com.yametech.yangjian.agent.core.eventsubscribe.eventbus.SubscribeEventBus;

/**
 * 
 * @Description 基于配置初始化EventMatcher
 * 
 * @author liuzhao
 * @date 2020年4月21日 下午3:14:22
 */
public class EventMatcherInit implements IConfigReader, SPI {
	private static final ILogger LOG = LoggerFactory.getLogger(EventMatcherInit.class);
	private volatile boolean init = false;
	private static final String EVENTGROUP_PREFIX = "eventSubscribe.group.";
	
	@Override
    public Set<String> configKey() {
        return new HashSet<>(Arrays.asList(EVENTGROUP_PREFIX.replaceAll("\\.", "\\\\.") + ".*", EventDispatcher.CONFIG_KEY_CALL_ASYNC.replaceAll("\\.", "\\\\.")));
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
        if(kv.containsKey(EventDispatcher.CONFIG_KEY_CALL_ASYNC)) {
			try {
    			if(Boolean.parseBoolean(kv.get(EventDispatcher.CONFIG_KEY_CALL_ASYNC))) {
    				SubscribeEventBus subscribeEventBus = new SubscribeEventBus();
    				EventDispatcher.setSubscribeEventBus(subscribeEventBus);
    				InstanceManage.registryInit(subscribeEventBus);
    			}
            } catch(Exception e) {
            	LOG.warn("{}配置错误：{}", EventDispatcher.CONFIG_KEY_CALL_ASYNC, kv.get(EventDispatcher.CONFIG_KEY_CALL_ASYNC));
            }
		}
        
        kv.entrySet().stream().filter(config -> config.getKey().startsWith(EVENTGROUP_PREFIX)).forEach(entry -> {
        	String eventGroup = entry.getKey();
        	String[] eventInfo = entry.getValue().split(">", 2);
        	if(eventInfo.length != 2) {
        		LOG.warn("配置错误：{} = {}", entry.getKey(), entry.getValue());
        		return;
        	}
        	String source = eventInfo[0].trim();
        	String target = eventInfo[1].trim();
        	InstanceManage.registry(new EventMatcher(eventGroup, getMatch(source)));
        	InstanceManage.registry(new SubscribeMatcher(eventGroup, new ClassMatch(getClass(target)), getMatch(target)));
        });
    }
    
    private static IConfigMatch getMatch(String source) {
    	String className = getClass(source);
    	return new MethodRegexMatch(".*" + source.replaceFirst(className, className.replaceAll("\\.", "\\\\\\\\.") + "\\\\"));
    }
    
    private static String getClass(String source) {
    	int index = source.indexOf('(');
    	if(index == -1) {
    		return source.substring(0, source.lastIndexOf('.'));
    	}
    	int pointIndex = source.substring(0, index).lastIndexOf('.');
    	return source.substring(0, pointIndex);
    }
    
}
