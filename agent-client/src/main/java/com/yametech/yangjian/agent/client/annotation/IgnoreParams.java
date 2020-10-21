package com.yametech.yangjian.agent.client.annotation;

import java.lang.annotation.*;

/**
 * 是否忽略订阅方法参数，如果方法上有该注解，则订阅方法中不需要匹配被订阅方法的参数，如果没有，则订阅方法参数必须包含被订阅方法参数，且位置相同
 * 订阅方法参数后面可多出3个参数：被订阅类实例、被订阅方法返回值、被订阅方法抛出的异常，这三个参数的位置放在最后，顺序不限先后
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreParams {

}
