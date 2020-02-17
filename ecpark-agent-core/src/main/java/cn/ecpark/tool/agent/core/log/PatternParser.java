package cn.ecpark.tool.agent.core.log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.ecpark.tool.agent.core.log.converter.SymbolConverter;

/**
 * @author zcn
 * @date: 2019-10-14
 * @description: 日志模式解析类
 **/
public class PatternParser {

    private Map<String, Class<? extends Converter>> allConverters;
    private char[] escapes = new char[]{ '[', ']', '-', ' ', '%' };

    public PatternParser(Map<String, Class<? extends Converter>> converters){
        this.allConverters = converters;
    }

    private boolean isEscape(char c){
        for(int i = 0; i < escapes.length; i++){
            if(escapes[i] == c){
                return true;
            }
        }
        return false;
    }

    private Converter createConverter(String symbol){
        Class<? extends Converter> clazz = allConverters.get(symbol);
        if(clazz == null){
            throw new IllegalStateException("Converter Can't be  found. Class: " + symbol);
        }

        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Create Converter error. Class: " + clazz, e);
        }
    }

    private Converter createEscapeConverter(String escape){
        return new SymbolConverter(escape);
    }

    public List<Converter> parse(String pattern){
        if(pattern.isEmpty() || pattern.trim().isEmpty()){
            throw new IllegalArgumentException("log pattern can not be null!");
        }

        List<Converter> converters = new ArrayList<>();
        int len = pattern.length();
        for(int i = 0; i < len; i++){
            char cur = pattern.charAt(i);
            if(cur == '%'){
                int start = ++i;
                while(i < len && !isEscape(pattern.charAt(i))){
                    i++;
                }
                converters.add(createConverter(pattern.substring(start, i--)));
            }else{
                converters.add(createEscapeConverter(new String(new char[]{cur})));
            }
        }
        return converters;
    }

}
