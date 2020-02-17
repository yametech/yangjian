package cn.ecpark.tool.util.eventbus.reactor.processor;


import java.time.Duration;

import cn.ecpark.tool.util.eventbus.reactor.consume.BaseConsume;

public interface EventBus<T> {

    /**
     * 添加订阅者，可添加多个监听者
     */
    void addSubscriber(BaseConsume<T> consume);

    /**
     * 发布消息
     * @param msg   发布的消息
     */
    void publish(T msg);

    /**
     * 关闭当前eventBus实例
     * @param duration  最大等待时间
     * @return  是否关闭成功
     */
    boolean shutdown(Duration duration);
    
    /**
     * 定义发布者名称
     * @return
     */
//    String name();
}
