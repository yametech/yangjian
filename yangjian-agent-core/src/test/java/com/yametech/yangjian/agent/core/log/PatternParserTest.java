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

import com.yametech.yangjian.agent.core.log.IConverter;
import com.yametech.yangjian.agent.core.log.converter.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zcn
 * @date: 2019-10-15
 **/
public class PatternParserTest {

    private static Map<String, Class<? extends IConverter>> CONVERTER_MAP;

    @Before
    public void before(){
        CONVERTER_MAP = new HashMap<>();
        CONVERTER_MAP.put("thread", ThreadConverter.class);
        CONVERTER_MAP.put("level", LevelConverter.class);
        CONVERTER_MAP.put("timestamp", TimestampConverter.class);
        CONVERTER_MAP.put("msg", MessageConverter.class);
        CONVERTER_MAP.put("throwable", ThrowableConverter.class);
        CONVERTER_MAP.put("class.method", ClassMethodConverter.class);
    }

    @Test
    public void parse(){
        PatternParser patternParser = new PatternParser(CONVERTER_MAP);
        LogEvent logEvent = new LogEvent(LogLevel.INFO, "message", new NullPointerException(), PatternParserTest.class.getCanonicalName());

        List<IConverter> coverters = patternParser.parse("%timestamp [%level]-[%thread]-[%class.method]: %msg %throwable");

        StringBuilder sb = new StringBuilder();
        coverters.forEach( converter -> sb.append(converter.convert(logEvent)) );

        Assert.assertEquals(18, coverters.size());
    }

}
