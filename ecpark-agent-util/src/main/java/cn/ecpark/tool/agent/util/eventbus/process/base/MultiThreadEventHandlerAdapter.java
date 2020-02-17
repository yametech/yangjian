package cn.ecpark.tool.agent.util.eventbus.process.base;

import cn.ecpark.tool.agent.util.eventbus.assignor.MultiThreadAssignor;
import cn.ecpark.tool.agent.util.eventbus.consume.BaseConsume;

public class MultiThreadEventHandlerAdapter<T> extends EventHandlerAdapter<T> {
    private MultiThreadAssignor<T> assignor;
    private int threadIndex;
    private int parallelism;

    public MultiThreadEventHandlerAdapter(BaseConsume<T> consume, int parallelism, int threadIndex, MultiThreadAssignor<T> assignor) {
        super(consume);
        this.threadIndex = threadIndex;
        this.assignor = assignor;
        this.parallelism = parallelism;
    }

    @Override
    public void onEvent(T event, long sequence, boolean endOfBatch) throws Exception {
        if (threadIndex == assignor.threadNum(event, parallelism) && consume.test(event)) {
            consume.accept(event);
        }
    }

}
