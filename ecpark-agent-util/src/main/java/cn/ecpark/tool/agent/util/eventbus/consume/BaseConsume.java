package cn.ecpark.tool.agent.util.eventbus.consume;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface BaseConsume<T> extends Consumer<T>, Predicate<T> {

	@Override
	default boolean test(T t) {
		return true;
	}
	
}
