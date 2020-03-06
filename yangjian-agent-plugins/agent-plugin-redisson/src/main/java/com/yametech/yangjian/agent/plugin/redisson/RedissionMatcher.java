/**
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

package com.yametech.yangjian.agent.plugin.redisson;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.IMetricMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.configmatch.ClassMatch;
import com.yametech.yangjian.agent.api.configmatch.CombineAndMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodNameMatch;

/**
 * 转换redission事件
 *
 * @author dengliming
 * @date 2019/12/5
 */
public class RedissionMatcher implements IMetricMatcher, IConfigReader {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("org.redisson.client.RedisConnection"),
                new MethodNameMatch("send")
        ));
    }

    @Override
    public String type() {
        return Constants.EventType.REDIS;
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("com.yametech.yangjian.agent.plugin.redisson.RedissionConvert");
    }
    
    private List<String> keyRules = new CopyOnWriteArrayList<>();// 此处会更改，如果不使用线程安全的List会导致convert中使用时异常

    @Override
    public Set<String> configKey() {
        return new HashSet<>(Arrays.asList("redis.key.rule", "redis.key.rule\\..*"));
    }

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
        keyRules.clear();// 此处没有原子的方法可以直接替换其中的元素，所以在有更新时可能导致短暂的无配置数据
        keyRules.addAll(kv.values());
    }

    @Override
    public Object getConfig() {
    	return keyRules;
    }
    
}
