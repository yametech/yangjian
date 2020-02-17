package cn.ecpark.tool.agent.util.eventbus.process;


import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;

public class EventBus<T> {
	private Disruptor<T> disruptor;
	protected RingBuffer<T> ringBuffer;

	public EventBus(Disruptor<T> disruptor) {
		this.disruptor = disruptor;
		this.ringBuffer = disruptor.start();
	}
	
    /**
     * 发布消息
     * @param msg   发布的消息
     */
    public boolean publish(Consumer<T> consumer) {
    	long sequence = ringBuffer.next();
    	return publish(consumer, sequence);
    }
    
    protected boolean publish(Consumer<T> consumer, long sequence) {
    	try {
			consumer.accept(ringBuffer.get(sequence));
		} finally {
			ringBuffer.publish(sequence);
		}
		return true;
	}
    
    /**
     * 谨慎使用，获取到sequence后必须执行publish
     * @return
     */
    public RingBuffer<T> getRingBuffer() {
    	return ringBuffer;
    }

    /**
     * 关闭当前eventBus实例，关闭后发布的消息会丢弃，会将队列中现有的消息消费完成或者超时
     * @param duration  最大等待时间
     * @return  是否关闭成功
     */
    public boolean shutdown(Duration duration) {
    	duration = duration == null ? Duration.ofSeconds(30) : duration;
    	try {
			disruptor.shutdown(duration.getSeconds(), TimeUnit.SECONDS);
			return true;
		} catch (TimeoutException e) {
			return false;
		}
    }
    
}
