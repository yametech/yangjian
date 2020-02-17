package cn.ecpark.tool.agent.core.log.converter;

import cn.ecpark.tool.agent.core.log.Converter;
import cn.ecpark.tool.agent.core.log.LogEvent;

/**
 * @author zcn
 * @date: 2019-10-15
 **/
public class SymbolConverter implements Converter {

    private String symbol;

    public SymbolConverter(String symbol){
        this.symbol = symbol;
    }

    @Override
    public String convert(LogEvent event) {
        return symbol;
    }
}
