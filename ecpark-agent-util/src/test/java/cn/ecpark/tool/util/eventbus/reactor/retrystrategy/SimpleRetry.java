package cn.ecpark.tool.util.eventbus.reactor.retrystrategy;

public class SimpleRetry implements IRetry {
    private int retryTimes = 3;// 重试次数，不含第一次调用
    private int intervalMillis = 100;// 重试间隔毫秒
    private int intervalFactor = 2;// 重试间隔因子

    public SimpleRetry retryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public SimpleRetry intervalMillis(int intervalMillis) {
        this.intervalMillis = intervalMillis;
        return this;
    }

    public SimpleRetry intervalFactor(int intervalFactor) {
        this.intervalFactor = intervalFactor;
        return this;
    }

    @Override
    public void call(Runnable run) {
        int thisRetryTimes = 0;
        Throwable throwable = null;
        do{
            try {
            	run.run();
                return;
            } catch(Throwable t) {
                throwable = t;
            }
            thisRetryTimes++;
            try {
                Thread.sleep(intervalMillis * intervalFactor * thisRetryTimes);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(thisRetryTimes <= retryTimes);
        throw new RuntimeException("重试" + (thisRetryTimes - 1) + "次，依然失败", throwable);
    }
}
