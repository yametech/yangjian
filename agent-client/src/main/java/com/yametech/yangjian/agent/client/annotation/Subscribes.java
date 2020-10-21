package com.yametech.yangjian.agent.client.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribes {
    Subscribe[] value();
}
