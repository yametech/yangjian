package cn.ecpark.tool.agent.api.interceptor;

/**
 * 通过实现该接口可以在构造方法调用后执行逻辑
 * 注意：实现类不能同时实现SPI接口
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年10月6日 下午9:47:43 
 * @param <T>
 */
public interface IConstructorListener {

    /**
     *	构造方法调用后前执行
     * @param thisObj   增强的类实例
     * @param allArguments  所有的方法参数
     */
	void constructor(Object thisObj, Object[] allArguments) throws Throwable;

}
