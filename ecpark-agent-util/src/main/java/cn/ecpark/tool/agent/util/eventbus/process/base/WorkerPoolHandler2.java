package cn.ecpark.tool.agent.util.eventbus.process.base;

//public class WorkerPoolHandler2<T> extends EventBus<T> implements WorkHandler<T> {
//	private BaseConsume<T> consume;
//	private Disruptor<T> disruptor;
//	
//	public WorkerPoolHandler2(BaseConsume<T> consume, EventFactory<T> factory, ThreadFactory threadFactory, WaitStrategy waitStrategy, int bufferSize) {
//		super(new Disruptor<>(factory, bufferSize, threadFactory, ProducerType.SINGLE, waitStrategy));
//		this.consume = consume;
//		disruptor = new Disruptor<>(factory, bufferSize, threadFactory, ProducerType.SINGLE, waitStrategy);
//		
//		disruptor.handleEventsWithWorkerPool(handlers);
//		disruptor.start();
//	}
//	
//	@Override
//	public void onEvent(T event) throws Exception {
//		
//	}
//	
//	
//	public boolean shutdown(Duration duration) {
//    	duration = duration == null ? Duration.ofSeconds(30) : duration;
//    	try {
//			disruptor.shutdown(duration.getSeconds(), TimeUnit.SECONDS);
//			return true;
//		} catch (TimeoutException e) {
//			return false;
//		}
//    }
//	
//}
