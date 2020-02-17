 package cn.ecpark.tool.util.disruptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class MethodEventHandler2 implements EventHandler<MethodEvent>, WorkHandler<MethodEvent> {
	private static final Logger log = LoggerFactory.getLogger(MethodEventHandler2.class);

	@Override
	public void onEvent(MethodEvent event) throws Exception {
		execute(event);
	}

	@Override
	public void onEvent(MethodEvent event, long sequence, boolean endOfBatch) throws Exception {
		execute(event);
	}

	private void execute(MethodEvent event) {
		log.info("consume:{}", event);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
