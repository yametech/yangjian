package cn.ecpark.tool.agent.api.base;

/**
 * 将实例对象转换为interception对象
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年12月21日 下午10:21:04
 */
public interface IInterceptorInit {
	
	/**
	 * 初始化Interceptor
	 * @param obj	返回值必须实现IConstructorListener、IMethodAOP、IStaticMethodAOP中的一个
	 * @param classLoader	类加载器
	 * @param type
	 */
	void init(Object obj, ClassLoader classLoader, MethodType type);
}
