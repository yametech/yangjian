package cn.ecpark.tool.util.disruptor;

import java.lang.reflect.Method;

public class MethodEvent {
	private Method method;// 调用的方法
	private Object[] arguments;// 方法参数
	private Long eventTime;// 发生时间毫秒数，可能值：方法调用前的时间、方法调用后的时间
	private Long startTime;// 方法调用前的时间毫秒数，如果是方法调用前执行事件，该值与eventTime一样
	private Throwable throwable;// 方法调用的异常信息，无异常时该值为null

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public Long getEventTime() {
		return eventTime;
	}

	public void setEventTime(Long eventTime) {
		this.eventTime = eventTime;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

}
