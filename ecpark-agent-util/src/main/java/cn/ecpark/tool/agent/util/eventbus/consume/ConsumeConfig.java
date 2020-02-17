package cn.ecpark.tool.agent.util.eventbus.consume;

import cn.ecpark.tool.agent.util.eventbus.assignor.MultiThreadAssignor;

public interface ConsumeConfig<T> {
	
	/**
     * 当前消费者的并行执行数量，如果大于1，实例必须线程安全
     * @return
     */
    default int parallelism() {
        return 1;
    }

    /**
     * 获取多线程消费时消息的分配方式，如果并行数为1，则此处可返回null
     * @return
     */
    default MultiThreadAssignor<T> assignor() {
        return null;
    }
    
    /**
     * 定义消费名称
     * @return
     */
    default String name() {
    	return "";
    }
}
