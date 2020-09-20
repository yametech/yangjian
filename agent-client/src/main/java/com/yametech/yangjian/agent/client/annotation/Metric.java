package com.yametech.yangjian.agent.client.annotation;

import java.lang.annotation.*;

/**
 * 标记需要统计qps/rt的方法
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Metric {

}
