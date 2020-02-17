package cn.ecpark.tool.agent.util.eventbus.process.base;

import com.lmax.disruptor.EventHandler;

import cn.ecpark.tool.agent.util.eventbus.consume.BaseConsume;

public class EventHandlerAdapter<T> implements EventHandler<T> {
    protected BaseConsume<T> consume;

    public EventHandlerAdapter(BaseConsume<T> consume) {
        this.consume = consume;
    }

    @Override
    public void onEvent(T event, long sequence, boolean endOfBatch) throws Exception {
        if (consume.test(event)) {
            consume.accept(event);
        }
    }

}
