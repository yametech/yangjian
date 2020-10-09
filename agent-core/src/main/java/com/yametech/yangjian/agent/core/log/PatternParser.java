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
package com.yametech.yangjian.agent.core.log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.yametech.yangjian.agent.core.log.converter.SymbolConverter;

/**
 * @author zcn
 * @date: 2019-10-14
 * @description: 日志模式解析类
 */
public class PatternParser {

    private Map<String, Class<? extends IConverter>> allConverters;
    private char[] escapes = new char[]{'[', ']', '-', ' ', '%'};

    public PatternParser(Map<String, Class<? extends IConverter>> converters) {
        this.allConverters = converters;
    }

    private boolean isEscape(char c) {
        for (int i = 0; i < escapes.length; i++) {
            if (escapes[i] == c) {
                return true;
            }
        }
        return false;
    }

    private IConverter createConverter(String symbol) {
        Class<? extends IConverter> clazz = allConverters.get(symbol);
        if (clazz == null) {
            throw new IllegalStateException("Converter Can't be  found. Class: " + symbol);
        }

        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Create Converter error. Class: " + clazz, e);
        }
    }

    private IConverter createEscapeConverter(String escape) {
        return new SymbolConverter(escape);
    }

    public List<IConverter> parse(String pattern) {
        if (pattern.isEmpty() || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("log pattern can not be null!");
        }

        List<IConverter> converters = new ArrayList<>();
        int len = pattern.length();
        for (int i = 0; i < len; i++) {
            char cur = pattern.charAt(i);
            if (cur == '%') {
                int start = ++i;
                while (i < len && !isEscape(pattern.charAt(i))) {
                    i++;
                }
                converters.add(createConverter(pattern.substring(start, i--)));
            } else {
                converters.add(createEscapeConverter(new String(new char[]{cur})));
            }
        }
        return converters;
    }

}
