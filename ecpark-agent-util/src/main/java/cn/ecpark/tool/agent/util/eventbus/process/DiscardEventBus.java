package cn.ecpark.tool.agent.util.eventbus.process;


import java.util.function.Consumer;

import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.dsl.Disruptor;

public class DiscardEventBus<T> extends EventBus<T> {
	
	public DiscardEventBus(Disruptor<T> disruptor) {
		super(disruptor);
	}
    /**
     * 发布消息
     * @param consumer   发布消费者，用于初始化事件值（事件实例是共用的），注意：如果缓存满了，consumer.accept的参数会是null，此时该数据会被丢弃
     */
	@Override
    public boolean publish(Consumer<T> consumer) {
    	long sequence = -1;
		try {
			sequence = ringBuffer.tryNext();
		} catch (InsufficientCapacityException e) {
			consumer.accept(null);
			return false;
		}
		return publish(consumer, sequence);
    }

}
