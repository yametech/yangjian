package cn.ecpark.tool.util.eventbus.reactor.retrystrategy;

public interface IRetry {
    void call(Runnable run);
}
