package cn.ecpark.tool.agent.api.interceptor;

import java.lang.reflect.Method;
import java.util.Map;

import cn.ecpark.tool.agent.api.bean.BeforeResult;

/**
 * 通过实现该接口可以在方法调用前后拦截执行逻辑，目前实现类包含：发送方法调用事件到内部队列由IMethodEventListener的实现类消费
 * 注意：实现类不能同时实现SPI接口
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年10月6日 下午9:47:43 
 * @param <T>
 */
public interface IMethodAOP<T> {

    /**
     *增强方法调用之前执行
     * @param thisObj   增强的类实例
     * @param allArguments  所有的方法参数
     * @param method    类方法定义实例
     * @return
     *  可为null
     */
	BeforeResult<T> before(Object thisObj, Object[] allArguments, Method method) throws Throwable;

    /**
     *增强方法调用之后执行
     * @param thisObj   增强的类实例
     * @param allArguments  所有的方法参数
     * @param method    类方法定义实例
     * @param beforeResult  before的返回值，可能为null
     * @param ret   方法实际返回值，如果before有设置返回值，该值也不一定为before中的返回值，可能后续拦截时重写了返回值
     * @param t 异常，没有则为null
     * @param globalVar value为before返回值中的globalVar，key为IMethodAOP实例class，没有则为null
     * @return
     */
	Object after(Object thisObj, Object[] allArguments, Method method, BeforeResult<T> beforeResult, Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable;

    /**
     * 增强方法处理异常时执行
     * @param thisObj   增强的类实例
     * @param allArguments  所有的方法参数
     * @param method    类方法定义实例
     * @param beforeResult  before的返回值，可能为null
     * @param t 异常
     */
    default void exception(Object thisObj, Object[] allArguments, Method method, BeforeResult<T> beforeResult, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {}

    /**
     * 是否启用该拦截器，用于热开启关闭
     * @return
     */
//    default boolean enable() {
//        return true;
//    }

}
