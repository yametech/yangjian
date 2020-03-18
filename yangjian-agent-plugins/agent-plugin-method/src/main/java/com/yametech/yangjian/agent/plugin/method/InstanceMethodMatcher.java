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
package com.yametech.yangjian.agent.plugin.method;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.IMetricMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.configmatch.CombineOrMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodRegexMatch;

/**
 * 拦截自定义实例方法
 *
 * @author liuzhao
 * @Description
 * @date 2019年10月9日 下午3:43:15
 */
public class InstanceMethodMatcher implements IMetricMatcher, IConfigReader {
	private List<IConfigMatch> matches;
	
//	@Override
//    public Set<String> configKey() {
//        return new HashSet<>(Arrays.asList("redis.key.rule", "redis.key.rule\\..*"));
//    }

    /**
     * 覆盖更新
     *
     * @param kv 配置数据
     */
    @Override
    public void configKeyValue(Map<String, String> kv) {
        if (kv == null) {
            return;
        }
        Set<String> configMatchesValues = new HashSet<>();
        kv.entrySet().forEach(entry -> configMatchesValues.addAll(Arrays.asList(entry.getValue().split("\r\n"))));
        matches = configMatchesValues.stream().map(match -> new MethodRegexMatch(match)).collect(Collectors.toList());
    }
	
    @Override
    public IConfigMatch match() {
    	if(matches == null || matches.isEmpty()) {
    		return null;
    	} else {
    		return new CombineOrMatch(matches);
    	}
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("com.yametech.yangjian.agent.plugin.method.InstanceMethodConvert");
    }
}
