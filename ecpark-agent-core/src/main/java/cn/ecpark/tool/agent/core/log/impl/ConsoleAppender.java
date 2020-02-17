package cn.ecpark.tool.agent.core.log.impl;

import cn.ecpark.tool.agent.core.log.Appender;
import cn.ecpark.tool.agent.core.log.LogEvent;

/**
 * @author zcn
 * @date: 2019-10-16
 **/
public class ConsoleAppender implements Appender {

    @Override
    public void append(LogEvent event) {
        System.out.println(event.getMessage());
    }
}
