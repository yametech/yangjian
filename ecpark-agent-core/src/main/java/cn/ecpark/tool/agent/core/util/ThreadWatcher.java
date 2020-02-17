package cn.ecpark.tool.agent.core.util;

import cn.ecpark.tool.agent.api.ISchedule;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 为了测试后面去掉
 *
 * @author dengliming
 * @date 2020/1/2
 */
public class ThreadWatcher implements ISchedule {
    private static final ILogger logger = LoggerFactory.getLogger(ThreadWatcher.class);
    private final static List<Thread> threadList = new ArrayList<>();

    public static void addThread(Thread thread) {
        threadList.add(thread);
    }

    @Override
    public int interval() {
        return 2;
    }

    @Override
    public void execute() {
        for (Thread thread : threadList) {
            logger.info("Thread[id:{},name:{},state:{}] print.", thread.getId(), thread.getName(), thread.getState(), thread.toString());
        }
    }
}
