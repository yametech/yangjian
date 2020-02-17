package cn.ecpark.tool.javaagent.log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.ecpark.tool.agent.core.log.Converter;
import cn.ecpark.tool.agent.core.log.LogEvent;
import cn.ecpark.tool.agent.core.log.LogLevel;
import cn.ecpark.tool.agent.core.log.PatternParser;
import cn.ecpark.tool.agent.core.log.converter.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zcn
 * @date: 2019-10-15
 **/
public class PatternParserTest {

    private static Map<String, Class<? extends Converter>> CONVERTER_MAP;

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

        List<Converter> coverters = patternParser.parse("%timestamp [%level]-[%thread]-[%class.method]: %msg %throwable");

        StringBuilder sb = new StringBuilder();
        coverters.forEach( converter -> sb.append(converter.convert(logEvent)) );

        Assert.assertEquals(18, coverters.size());
    }

}
