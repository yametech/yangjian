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
package com.yametech.yangjian.agent.plugin.lettuce.metric;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.convert.IMethodAsyncConvert;
import com.yametech.yangjian.agent.plugin.lettuce.bean.RedisKeyBean;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.RedisCommand;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author dengliming
 * @date 2020/6/14
 */
public class ChannelWriteConvert implements IMethodAsyncConvert, IConfigReader {

    private List<String> keyRules = new CopyOnWriteArrayList<>();

    @Override
    public List<Object> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
                                Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        List<String> keys = new ArrayList<>();
        if (allArguments[0] instanceof RedisCommand) {
            RedisCommand redisCommand = (RedisCommand) allArguments[0];
            keys.add(getCommandKey(redisCommand.getArgs()));
        } else if (allArguments[0] instanceof Collection) {
            @SuppressWarnings("unchecked") Collection<RedisCommand> redisCommands = (Collection<RedisCommand>) allArguments[0];
            for (RedisCommand redisCommand : redisCommands) {
                keys.add(getCommandKey(redisCommand.getArgs()));
            }
        }

        long now = System.currentTimeMillis();
        return Arrays.asList(new RedisKeyBean(keys, now, now - startTime));
    }

    @Override
    public List<TimeEvent> convert(Object eventBean) {
        RedisKeyBean redisKeyBean = (RedisKeyBean) eventBean;
        return getMatchKeyTimeEvents(redisKeyBean);
    }

    public List<TimeEvent> getMatchKeyTimeEvents(RedisKeyBean redisKeyBean) {
        Map<String, Integer> matchKeyNums = new HashMap<>();
        for (String key : redisKeyBean.getKeys()) {
            Set<String> matchKeyRules = getMatchKeyRules(key);
            if (matchKeyRules == null) {
                continue;
            }
            for (String keyRule : matchKeyRules) {
                Integer num = matchKeyNums.getOrDefault(keyRule, 0);
                matchKeyNums.put(keyRule, num + 1);
            }
        }
        return matchKeyNums.entrySet()
                .stream()
                .map(o -> {
                    TimeEvent timeEvent = new TimeEvent();
                    timeEvent.setEventTime(redisKeyBean.getEventTime());
                    timeEvent.setUseTime(redisKeyBean.getUseTime());
                    timeEvent.setIdentify(o.getKey());
                    timeEvent.setNumber(o.getValue());
                    return timeEvent;
                })
                .collect(Collectors.toList());
    }

    private Set<String> getMatchKeyRules(String key) {
        if (StringUtil.isEmpty(key) || keyRules == null) {
            return null;
        }
        return keyRules.stream()
                .filter(r -> key.indexOf(r) != -1)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> configKey() {
        return new HashSet<>(Arrays.asList("redis\\.key\\.rule", "redis\\.key\\.rule\\..*"));
    }

    @Override
    public void configKeyValue(Map<String, String> kv) {
        if (kv == null) {
            return;
        }
        keyRules.clear();// 此处没有原子的方法可以直接替换其中的元素，所以在有更新时可能导致短暂的无配置数据
        keyRules.addAll(kv.values());
    }

    private String getCommandKey(CommandArgs commandArgs) {
        try {
            return StandardCharsets.UTF_8.decode(commandArgs.getFirstEncodedKey()).toString();
        } catch (Exception e) {
            // can't happen
        }
        return null;
    }
}
