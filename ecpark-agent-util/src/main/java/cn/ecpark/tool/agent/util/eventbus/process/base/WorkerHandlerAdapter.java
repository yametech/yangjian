package cn.ecpark.tool.agent.util.eventbus.process.base;

import com.lmax.disruptor.WorkHandler;

import cn.ecpark.tool.agent.util.eventbus.consume.BaseConsume;

public class WorkerHandlerAdapter<T> implements WorkHandler<T> {
    private BaseConsume<T> consume;

    public WorkerHandlerAdapter(BaseConsume<T> consume) {
        this.consume = consume;
    }

    @Override
    public void onEvent(T event) throws Exception {
        if (consume.test(event)) {
            consume.accept(event);
        }
    }

}
