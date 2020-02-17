package cn.ecpark.tool.util.disruptor;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.RingBuffer;

public class EventProducer {
	private static Logger log = LoggerFactory.getLogger(EventProducer.class);
	private RingBuffer<MethodEvent> ringBuffer;

	public EventProducer(RingBuffer<MethodEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public boolean onData(Method method, Object[] arguments, Long eventTime, Long startTime, Throwable throwable) {
//		log.info("1");
		long sequence = ringBuffer.next();
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
