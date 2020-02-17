package cn.ecpark.tool.agent.core.log;

/**
 * @author zcn
 * @date: 2019-10-14
 **/
public interface Appender {
    void append(LogEvent event);
}
