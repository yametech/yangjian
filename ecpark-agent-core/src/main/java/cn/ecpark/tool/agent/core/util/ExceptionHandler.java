package cn.ecpark.tool.agent.core.util;

import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;

/**
 * @author dengliming
 * @date 2020/1/2
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final ILogger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.error(e, "Uncaught Exception in Thread:({}/{}/{}).", t.getId(), t.getName(), t.getState());
    }
}
