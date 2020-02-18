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

package com.yametech.yangjian.agent.core.log.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.core.config.Config;
import com.yametech.yangjian.agent.core.log.Appender;
import com.yametech.yangjian.agent.core.log.AppenderFactory;
import com.yametech.yangjian.agent.core.log.Converter;
import com.yametech.yangjian.agent.core.log.ILogger;
import com.yametech.yangjian.agent.core.log.LogEvent;
import com.yametech.yangjian.agent.core.log.LogLevel;
import com.yametech.yangjian.agent.core.log.LoggerFactory;
import com.yametech.yangjian.agent.core.log.PatternParser;
import com.yametech.yangjian.agent.core.log.converter.ClassMethodConverter;
import com.yametech.yangjian.agent.core.log.converter.LevelConverter;
import com.yametech.yangjian.agent.core.log.converter.MessageConverter;
import com.yametech.yangjian.agent.core.log.converter.ThreadConverter;
import com.yametech.yangjian.agent.core.log.converter.ThrowableConverter;
import com.yametech.yangjian.agent.core.log.converter.TimestampConverter;

/**
 * @author zcn
 * @date: 2019-10-14
 * @description: 格式化日志输出类。
 **/
public class PatternLogger implements ILogger {

    private static Map<String, Class<? extends Converter>> CONVERTER_MAP;

    private String pattern;
    private String clazz;
    private Appender appender;
    private LogLevel level;
    private List<Converter> converters;

    static {
        CONVERTER_MAP = new HashMap<>();
        CONVERTER_MAP.put("thread", ThreadConverter.class);
        CONVERTER_MAP.put("level", LevelConverter.class);
        CONVERTER_MAP.put("timestamp", TimestampConverter.class);
        CONVERTER_MAP.put("msg", MessageConverter.class);
        CONVERTER_MAP.put("throwable", ThrowableConverter.class);
        CONVERTER_MAP.put("class.method", ClassMethodConverter.class);
    }

    public PatternLogger(String pattern, String clazz){
        this.pattern = pattern;
        this.clazz = clazz;
        this.appender = AppenderFactory.buildAppender();
        this.level = LogLevel.valueOf(Config.getKv(Constants.LOG_LEVEL, LoggerFactory.DEFAULT_LEVEL.name()));
        this.converters = new PatternParser(CONVERTER_MAP).parse(this.pattern);
    }

    public PatternLogger(String pattern, Class<?> clazz){
        this(pattern, clazz.getCanonicalName());
    }

    private boolean isLevelEnable(LogLevel logLevel){
        return logLevel.compareTo(level) >= 0;
    }

    private void logIfEnabled(LogLevel logLevel, String format){
        if(isLevelEnable(logLevel)){
            log(new LogEvent(logLevel, format, null, clazz));
        }
    }

    private void logIfEnabled(LogLevel logLevel, String format, Throwable throwable, Object... arguments){
        if(isLevelEnable(logLevel)){
            log(new LogEvent(logLevel, replaceParam(format, arguments), throwable, clazz));
        }
    }

    private void log(LogEvent logEvent){
        StringBuilder sb = new StringBuilder();
        for(Converter converter : converters){
            sb.append(converter.convert(logEvent));
        }
        logEvent.setMessage(sb.toString());
        appender.append(logEvent);
    }

    private String replaceParam(String message, Object... parameters) {
        int start = 0;
        int parametersIndex = 0;
        int index;
        String tmpMessage = message;
        while ((index = message.indexOf("{}", start)) != -1) {
            if (parametersIndex >= parameters.length) {
                break;
            }
            tmpMessage = tmpMessage.replaceFirst("\\{\\}", Matcher.quoteReplacement(String.valueOf(parameters[parametersIndex++])));
            start = index + 2;
        }
        return tmpMessage;
    }

    @Override
    public boolean isDebugEnable() {
        return isLevelEnable(LogLevel.DEBUG);
    }

    @Override
    public boolean isInfoEnable() {
        return isLevelEnable(LogLevel.INFO);
    }

    @Override
    public boolean isWarnEnable() {
        return isLevelEnable(LogLevel.WARN);
    }

    @Override
    public boolean isErrorEnable() {
        return isLevelEnable(LogLevel.ERROR);
    }

    @Override
    public void debug(String format) {
        logIfEnabled(LogLevel.DEBUG, format);
    }

    @Override
    public void debug(String format, Object... arguments) {
        logIfEnabled(LogLevel.DEBUG, format, null, arguments);
    }

    @Override
    public void info(String format) {
        logIfEnabled(LogLevel.INFO, format);
    }

    @Override
    public void info(String format, Object... arguments) {
        logIfEnabled(LogLevel.INFO, format, null, arguments);
    }

    @Override
    public void warn(String format) {
        logIfEnabled(LogLevel.WARN, format);
    }

    @Override
    public void warn(String format, Object... arguments) {
        logIfEnabled(LogLevel.WARN, format, null, arguments);
    }

    @Override
    public void warn(Throwable e, String format, Object... arguments) {
        logIfEnabled(LogLevel.WARN, format, e, arguments);
    }

    @Override
    public void error(String format) {
        logIfEnabled(LogLevel.ERROR, format);
    }

    @Override
    public void error(String format, Object... arguments) {
        logIfEnabled(LogLevel.ERROR, format, null, arguments);
    }

    @Override
    public void error(Throwable e, String format, Object... arguments) {
        logIfEnabled(LogLevel.ERROR, format, e, arguments);
    }
}
