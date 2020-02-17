package cn.ecpark.tool.agent.core.log.converter;

import cn.ecpark.tool.agent.core.log.Converter;
import cn.ecpark.tool.agent.core.log.LogEvent;

/**
 * @author zcn
 * @date: 2019-10-14
 **/
public class ThreadConverter implements Converter {
    @Override
    public String convert(LogEvent event) {
        return Thread.currentThread().getName();
    }
}
