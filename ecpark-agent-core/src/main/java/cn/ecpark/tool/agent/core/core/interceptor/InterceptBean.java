package cn.ecpark.tool.agent.core.core.interceptor;

import cn.ecpark.tool.agent.api.bean.BeforeResult;

public class InterceptBean<T> {
    private T interceptor;
    private BeforeResult<?> result;

    public InterceptBean(T interceptor, BeforeResult<?> result) {
        this.interceptor = interceptor;
        this.result = result;
    }

    public T getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(T interceptor) {
        this.interceptor = interceptor;
    }

    public BeforeResult<?> getResult() {
        return result;
    }

    public void setResult(BeforeResult<?> result) {
        this.result = result;
    }
}
