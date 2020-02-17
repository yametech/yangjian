package cn.ecpark.tool.util.disruptor;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.RingBuffer;

public class EventDiscardProducer {
	private static Logger log = LoggerFactory.getLogger(EventDiscardProducer.class);
	private RingBuffer<MethodEvent> ringBuffer;
//	private boolean discardFull = false;// 缓存满了之后是否丢弃最新产生的数据
	private Consumer<MethodEvent> discardConsume;// 监听缓存满了丢弃的最新数据，可为null，非null时一定要注意处理速度

	public EventDiscardProducer(RingBuffer<MethodEvent> ringBuffer, boolean discardFull, Consumer<MethodEvent> discardConsume) {
		this.ringBuffer = ringBuffer;
//		this.discardFull = discardFull;
		this.discardConsume = discardConsume;
	}

	public boolean onData(Method method, Object[] arguments, Long eventTime, Long startTime, Throwable throwable) {
//		log.info("1");
		long sequence = -1;
		try {
			sequence = ringBuffer.tryNext();
		} catch (InsufficientCapacityException e) {
			if(discardConsume != null) {
				discardConsume.accept(setFields(new MethodEvent(), method, arguments, eventTime, startTime, throwable));
			}
			return false;
		}
//		log.info("2");
		try {
			MethodEvent event = ringBuffer.get(sequence);
//			log.info("3");
			setFields(event, method, arguments, eventTime, startTime, throwable);
		} finally {
			ringBuffer.publish(sequence);
//			log.info("4");
		}
		return true;
	}
	
	private MethodEvent setFields(MethodEvent event, Method method, Object[] arguments, Long eventTime, Long startTime, Throwable throwable) {
		event.setMethod(method);
		event.setArguments(arguments);
		event.setEventTime(eventTime);
		event.setStartTime(startTime);
		event.setThrowable(throwable);
		return event;
	}
}
