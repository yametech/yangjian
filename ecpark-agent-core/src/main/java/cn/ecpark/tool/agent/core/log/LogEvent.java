package cn.ecpark.tool.agent.core.log;

/**
 * @author zcn
 * @date: 2019-10-14
 **/
public class LogEvent {

    private LogLevel level;
    private String message;
    private Throwable throwable;
    private String targetClass;

    public LogEvent(LogLevel level, String message, Throwable throwable, String targetClass){
        this.level = level;
        this.message = message;
        this.throwable = throwable;
        this.targetClass = targetClass;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getTargetClass() {
        return targetClass;
    }
}
