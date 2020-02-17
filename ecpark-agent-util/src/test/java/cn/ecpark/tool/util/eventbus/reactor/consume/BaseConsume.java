package cn.ecpark.tool.util.eventbus.reactor.consume;

import java.util.function.Consumer;
import java.util.function.Predicate;

import cn.ecpark.tool.util.eventbus.reactor.assignor.MultiThreadAssignor;

public interface BaseConsume<T> extends Consumer<T>,Predicate<T> {

	@Override
	default boolean test(T t) {
		return true;
	}
	
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
