package com.yametech.yangjian.agent.client.annotation;

import java.lang.annotation.*;

/**
 * 定义被订阅方法
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Subscribes.class)
public @interface Subscribe {
    /**
     * 被订阅类匹配的接口，任何一个匹配到（包含多级接口）则匹配成功，interfaces、parent、className必须配置至少一个
     * 注意：一定不能使用Xxx.class获取String
     * @return  多个接口字符串，如：{"com.yametech.yangjian.agent.client.IStatusCollect", "com.yametech.yangjian.agent.client.IStatusCollect"}
     */
    String[] interfaces() default {};

    /**
     * 被订阅类匹配的父类(多级父类时，任何一个匹配到，则匹配成功)，interfaces、parent、className必须配置至少一个
     * 注意：一定不能使用Xxx.class获取String
     * @return  父类字符串，如："com.mongodb.Mongo"
     */
    String parent() default "";

    /**
     * 注意：一定不能使用Xxx.class获取String
     * @return  被订阅类的类名，interfaces、parent、className必须配置至少一个
     */
    String className() default "";

    /**
     * @return  被订阅类匹配的方法名，如：test
     */
    String methodName() default "";

    /**
     * @return  被订阅类匹配的方法名正则表达式，如：test
     */
    String methodNameRegex() default "";

    /**
     * @return  被订阅方法的参数个数，如：argumentNumber=0
     */
    int argumentNumber() default -1;

    /**
     *
     * @return  被订阅方法的对应位置的参数类型，如果不指定某个位置的参数类型，可在对应位置使用空字符串，如：argumentsType={"java.lang.Integer", "", "java.lang.String"}
     */
    String[] argumentType() default {};

}
