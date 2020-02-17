package cn.ecpark.tool.agent.core.log;

import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.core.config.Config;
import cn.ecpark.tool.agent.core.log.impl.ConsoleAppender;
import cn.ecpark.tool.agent.core.log.impl.RollingFileAppender;

/**
 * @author zcn
 * @date: 2019-10-16
 **/
public class AppenderFactory {

    private static Appender appender = null;

    public static Appender buildAppender(){
        if(appender != null) {
            return appender;
        }else{
            synchronized (AppenderFactory.class){
                if(appender != null){
                    return appender;
                }
                LogOutput output = LogOutput.valueOf(Config.getKv(Constants.LOG_OUTPUT, LoggerFactory.DEFAULT_OUTPUT.name()));
                switch (output){
                    case FILE:
                        return appender =  new RollingFileAppender();
                    case CONSOLE:
                        return appender =  new ConsoleAppender();
                    default:
                        throw new IllegalStateException("don't support log output : " + output);
                }
            }
        }
    }

}
