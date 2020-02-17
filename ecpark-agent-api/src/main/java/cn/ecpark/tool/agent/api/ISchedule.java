package cn.ecpark.tool.agent.api;

import cn.ecpark.tool.agent.api.base.SPI;

import java.util.concurrent.TimeUnit;

/**
 * 实现该接口即可定时调用execute方法，目前用于定时打印日志
 *
 * @author liuzhao
 * @Description
 * @date 2019年10月12日 上午9:12:36
 */
public interface ISchedule extends SPI {

    /**
     * 首次执行的延迟时间
     *
     * @return 秒数
     */
    default int initialDelay() {
        return 0;
    }

    /**
     * 时间间隔单位（默认秒）
     *
     * @return
     */
    default TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }

    /**
     * 调度间隔
     *
     * @return 秒数
     */
    int interval();

    /**
     * 执行调度任务
     */
    void execute();
}
