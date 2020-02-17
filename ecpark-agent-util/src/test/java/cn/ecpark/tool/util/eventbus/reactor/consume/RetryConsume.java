package cn.ecpark.tool.util.eventbus.reactor.consume;


import cn.ecpark.tool.util.eventbus.reactor.retrystrategy.IRetry;
import cn.ecpark.tool.util.eventbus.reactor.retrystrategy.SimpleRetry;

public abstract class RetryConsume<T> extends GenericMatchConsume<T> {
    private IRetry retry = new SimpleRetry();

    public RetryConsume() {}

    public RetryConsume(IRetry retry) {
        this.retry = retry;
    }

    @Override
    public final void accept(T msg) {
        retry.call(() -> acceptRetry(msg));
    }

    public abstract void acceptRetry(T msg);

}
