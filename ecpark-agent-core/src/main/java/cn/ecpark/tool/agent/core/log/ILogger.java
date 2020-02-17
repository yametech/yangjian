package cn.ecpark.tool.agent.core.log;

/**
 * @author zcn
 * @date: 2019-10-14
 **/
public interface ILogger {

    boolean isDebugEnable();

    boolean isInfoEnable();

    boolean isWarnEnable();

    boolean isErrorEnable();

    void debug(String format);

    void debug(String format, Object... arguments);

    void info(String format);

    void info(String format, Object... arguments);

    void warn(String format);

    void warn(String format, Object... arguments);

    void warn(Throwable e, String format, Object... arguments);

    void error(String format);

    void error(String format, Object... arguments);

    void error(Throwable e, String format, Object... arguments);
}
